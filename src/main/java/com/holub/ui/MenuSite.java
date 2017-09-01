package com.holub.ui;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MenuSite
{
    private static JFrame menuFrame = null;
    private static JMenuBar menuBar = null;

    private static Map requesters = new HashMap();

    private static Properties nameMap;

    private static Pattern shortcutExtractor =
        Pattern.compile(
                "\\s*([^;]+?)\\s" // value
                + "(;\\s*([^\\s].*?))?\\s*$" ); // ; shorttcut

    private static Pattern submenuExtractor =
        Pattern.compile(
                "(.*?)(?::(.*?))?"
                + "(?::(.*?))?"
                + "(?::(.*?))?"
                + "(?::(.*?))?"
                + "(?::(.*?))?"
                + "(?::(.*?))?" );

    private static final LinkedList menuBarContents =
        new LinkedList();

    private MenuSite(){}

    private static boolean valid()
    {
        assert menuFrame != null : "MenuSite not established";
        assert menuBar != null : "MenuSite not established";
        return true;
    }

    public synchronized static void establish(JFrame container)
    {
        assert container != null;
        assert menuFrame == null:
            "Tried to establish more than one MenuSite";

        menuFrame = container;
        menuFrame.setJMenuBar( menuBar = new JMenuBar() );

        assert valid();
    }

    public static void addMenu(Object requester, String menuSpecifier) 
    {
        createSubmenuByName( requester, menuSpecifier );
    }

    public static void addLine( Object requester,
            String toThisMenu,
            String name,
            ActionListener listener )
    {
        assert requester !=: "null requester";
        assert name != "null item";
        assert toThisMenu != null: "null toThisMenu";
        assert valid();

        /* 'element' 필드가 이곳에 있는 이유는 else 절의 assert에
         * 걸렸을 경우 메뉴를 생성하지 않도록 하기 위햇서이다.
         * 이러한 고려를 하지 않는다면 if 절과 else 절에서 아이템을
         * 생성할 수 있을 것이다. */

        Component element;

        if ( name.equals( "-" ))
            element = new JSeparator();
        else 
        {
            assert listener != null: "null listener";

            JMenuItem lineItem = new JMenuItem( name );
            lineItem.setName( name );
            lineItem.addActionListener( listener );
            setLableAndShortcut( lineItem );

            element = lineItem;
        }

        JMenu found = createSubmenuByName( requester, toThisMenu );
        if ( found == null )
            throw new IllegalArgumentException(
                    "addLine() can't find menu (" + toThisMenu + ")" );

        Item item = new Item( element, found, toThisMenu );
        menusAddedBy( requester ).add( item );
        item.attachYourselfToYourParent();

    }

    public static void removeMyMenus( Object requester )
    {
        if( requester == null )
            throw new IllegalArgumentException( "null requester" );
        assert valid();

        Collection allItems = (Collection)( requesters.remove( requester ));

        if( allItems != null )
        {
            Iterator i = allItems.iterator();
            while( i.hasNext() )
            {
                Item current = (Item) i.next();
                current.detachYourselfFromYourParent();
            }
        }
    }

    public static void setEnable( Object requester, boolean enable )
    {
        assert requester != null;
        assert valid();

        Collection allItems = (Collection)( requesters.get( requester ));

        if( allItems != null )
        {
            Iterator i = allItems.iterator();
            while( i.hasNext() )
            {
                Item current = (Item) i.next();
                current.setEnableAttribute( enable );
            }

        }
    }

    public static JMenuItem getMyMenuItem( Object requester,
            String menuSpecifier, String name)
    {
        assert requester != null;
        assert menuSpecifier != null;
        assert valid();

        Collection allItems = (Collection) ( requesters.get( requester ) );

        if( allItems != null )
        {
            Iterator i = allItems.iterator();
            while( i.hasNext() )
            {
                Item current = (Item) i.next();
                if( current.specifiedBy( menuSpecifier ) )
                {
                    if( current.item() instanceof JSeparator )
                        continue;

                    if( name == null && current.item() instanceof JMenu )
                        return (JMenu)( current.item() );

                    if(( (JMenuItem)current.item()).getName().equals( name ))
                        return (JMenuItem) current.item();
                }
            }
        }
        return null;
    }

    /* =====================================================================
     * private 메소드와 크래스들
     * ===================================================================== */

    private static JMenu createSubmenuByName(Object requester, String menuSpecifier)
    {
        assert requester != null;
        assert menuSpecifier !=  null;
        assert valid();
        
        Matcher m = submenuExtractor.matcher( menuSpecifier );
        if( !m.matches() )
            throw new IllegalArgumentException(
                    "Malformed menu specifier." );
        // null이면 메뉴 바에서 검색을 시작한다.
        // null이 아니면 "parent"가 지정하는 메뉴에서 검색을 시작한다.

        JMenuItem child = null;
        MenuElement parent = menuBar;
        String childName;

        for( int i = 1; (childName = m.group(i++)) != null; parent = child)
        {
            child = getSubmenuByName( childName, parent.getComponent() );

            if( child != null )
            {
                if( !(child instanceof JMenu) ) // 라인 아이템이다!
                    throw new IllegalArgumentException(
                            "Specifier identifies line item, not Menu." );
            }
            else // 존재하지 않으므로 생성한다.
            {
                child = new JMenu( childName );
                child.setName( childName );
                setLableAndShortcut( child );

                Item item = new Item( child, parent, menuSpecifier );
                menuAddedBy( requester ).add( item );
                item.attatchYourselfToYourParent();

            }
        }

        return (JMenu)child;
    }

    private static void setLableAndShortcut( JMenuItem item )
    {
        String name = item.getName();
        if( name == null )
            return;
    }

} 
