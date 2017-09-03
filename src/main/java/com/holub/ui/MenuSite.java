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
                + "(;\\s*([^\\s].*?))?\\s*$" ); // ; shortcut

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

    private static JMenuItem getSubmenuByName( String name,
            MenuElement[] contents )
    {
        JMenuItem found = null;
        for( int i = 0; found == null && i < contents.length ; ++i )
        {
            /* 시스템은 빈 서브 메뉴의 경우에는 내부 팝업 메뉴를
             * 생성한다. 이러한 경우 팝업의 내용에서 'name'을
             * 찾아 본다.
             * PopupMenu와 JMenuItem이 같은 인터페이스를 구현했다면,
             * 이 작업이 훨씬 쉬었겠지만 아쉽게도 그렇지 않다.
             * 클래스 어댑터(adapter)를 사용하여 이 둘이 공통의
             * 인터페이스를 구현하고 있는 것처럼 보이게 할 수도 없다.
             * JPopupWindows는 내가 아니라 스윙이 생성하기 때문이다. */

            if( contents[i] instanceof JPopupMenu )
                found = getSubmenuByName( name,
                        ((JPopupMenu)contents[i]).getSubElements());
            else if( ((JMenuItem)contents[i]).getName().equals( name ) )
            {
                found = (JMenuItem) contents[i];
            }
        }
        return found;
    }

    private static void mapNames( URL table ) throws IOException
    {
        if( nameMap == null )
            nameMap = new Properties();
        nameMap.load( table.openStream() );
    }

    public static void addMapping( String name, String label,
            String shortcut )
    {
        if( nameMap == null )
            nameMap = new Properties();
        nameMap.put( name, label + ";" + shortcut );
    }

    private static void setLableAndShortcut( JMenuItem item )
    {
        String name = item.getName();
        if( name == null )
            return;
        
        String label;

        if( nameMap != null
            && (label = (String) (nameMap.get( name ))) != null )
        {
            Matcher m = shortcutExtractor.matcher( label );
            if( !m.matches() ) // 잘못된 형식의 인풋 라인
            {
                item.setText( name );
                Logger.getLogger( "com.holub.ui" ).warning(
                        "Bad"
                        + "name-to-label maep entry:"
                        + "\n\tiput=[" + name + "=" + label + "]"
                        + "\n\tSetting label to " + name );
            }
            else
            {
                item.setText( m.group(1) );

                String shortcut = m.group(3);

                if( shortcut != null )
                {
                    if( shortcut.length() == 1 )
                    {
                        item.setAccelerator(
                                KeyStroke.getKeyStroke(
                                    shortcut.toUpperCase().charAt(0),
                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),
                                    false ));
                    }
                    else
                    {
                        KeyStroke key = KeyStroke.getKeyStroke( shortcut );
                        if( key != null )
                            item.setAccelerator( key );
                        else
                        {
                            Logger.getLogger( "com.holub.ui" ).warning(
                                    "Malformed shortcut parent spectification "
                                    + "in MenuSite map file: "
                                    + shortcut );
                        }
                    }
                }
            }
        }
    }

    private static Collection menusAddedBy( Object requester )
    {
        assert requester != null: "Bad argument";
        assert requesters != null: "No requesters";
        assert valid();

        Collection menus = (Collection) (requesters.get( requester ));
        if( menus == null )
        {
            menus = new LinkedList();
            requesters.put( requester, menus );
        }
        return menus;
    }

    private static final class Item
    {
        // private JMenuItem item;
        private  Component item;

        private String parentSpecification; // JMenu 혹은 JMenuItem의 부모
        private MenuElement parent; // JMenu 혹은 JMenuBar
        private boolean isHelpMenu;

        public String toString()
        {
            StringBuffer b = new StringBuffer( parentSpecification );
            if( item instanceof JMenuItem )
            {
                JMenuItem i = (JMenuItem) item;
                b.append( ":" );
                b.append( i.getName() );
                b.append( " (" );
                b.append( i.getText() );
                b.append( ")" );
            }
            return b.toString();
        }

        private boolean valid()
        {
            assert item != null : "item is null";
            assert parent != null : "parent is null";
            return true;
        }

        public Item( Component item, MenuElement parent,
                String parentSpecification )
        {
            assert parent != null;
            assert parent instanceof JMenu || parent instanceof JMenuBar
                : "Parent must be JMenu or JMenuBar";

            this.item = item;
            this.parent = parent;
            this.parentSpecification = parentSpecification;
            this.isHelpMenu =
                ( item instanceof JMenuItem )
                && ( item.getName().compareToIgnoreCase( "help" ) == 0);
            assert valid();
        }

        public boolean specifiedBy( String specifier )
        {
            return parentSpecification.equals( specifier );
        }

        public Component item()
        {
            return item;
        }
    }

} 
