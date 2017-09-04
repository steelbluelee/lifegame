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

    private static Map<Object, LinkedList<Item>> requesters =
        new HashMap<Object, LinkedList<Item>>();

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

    private static final LinkedList<Item> menuBarContents =
        new LinkedList<Item>();

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
        assert requester != "null requester";
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

        Collection<Item> allItems =
            (Collection<Item>)( requesters.remove( requester ));

        if( allItems != null )
        {
            Iterator<Item> i = allItems.iterator();
            while( i.hasNext() )
            {
                Item current = i.next();
                current.detachYourselfFromYourParent();
            }
        }
    }

    public static void setEnable( Object requester, boolean enable )
    {
        assert requester != null;
        assert valid();

        Collection<Item> allItems =
            (Collection<Item>)( requesters.get( requester ));

        if( allItems != null )
        {
            Iterator<Item> i = allItems.iterator();
            while( i.hasNext() )
            {
                Item current = i.next();
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

        Collection<Item> allItems =
            (Collection<Item>) ( requesters.get( requester ) );

        if( allItems != null )
        {
            Iterator<Item> i = allItems.iterator();
            while( i.hasNext() )
            {
                Item current = i.next();
                if( current.specifiedBy( menuSpecifier ) )
                {
                    if( current.item() instanceof JSeparator )
                        continue;

                    if( name == null && current.item() instanceof JMenu )
                        return (JMenu)( current.item() );

                    if(((JMenuItem) current.item()).getName().equals( name ))
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
            child = getSubmenuByName( childName, parent.getSubElements() );

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
                menusAddedBy( requester ).add( item );
                item.attachYourselfToYourParent();

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

    private static Collection<Item> menusAddedBy( Object requester )
    {
        assert requester != null: "Bad argument";
        assert requesters != null: "No requesters";
        assert valid();

        LinkedList<Item> menus =  requesters.get( requester );
        if( menus == null )
        {
            menus = new LinkedList<Item>();
            requesters.put( requester, menus );
        }
        return (Collection<Item>) menus;
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

        public final void attachYourselfToYourParent()
        {
            assert valid();

            if( parent instanceof JMenu )
            {
                ((JMenu) parent).add( item );
            }
            else if( menuBarContents.size() <= 0 )
            {
                menuBarContents.add( this );
                ((JMenuBar) parent).add( item );
            }
            else
            {
                Item last = (Item) (menuBarContents.getLast());
                if( !last.isHelpMenu )
                {
                    menuBarContents.addLast( this );
                    ((JMenuBar) parent).add( item );
                }
                else // help 메뉴를 삭제하고 새로운
                {    // 아이템을 추가한다. 그 후 help 메뉴를
                     // 다시 넣는다.
                    
                    menuBarContents.removeLast();
                    menuBarContents.add( this );
                    menuBarContents.add( last );

                    if( parent == menuBar )
                        parent = regenerateMenuBar();
                }
            }
        }

        public void detachYourselfFromYourParent()
        {
            assert valid();

            if( parent instanceof JMenu )
            {
                ((JMenu) parent).remove( item );
            }
            else // parent의 메뉴 바
            {
                menuBar.remove( item );
                menuBarContents.remove( this );
                regenerateMenuBar(); // whithour me on it

                parent = null;
            }
        }

        public void setEnableAttribute( boolean on )
        {
            if( item instanceof JMenuItem )
            {
                JMenuItem item = (JMenuItem) this.item;
                item.setEnabled( on );
            }
        }

        private JMenuBar regenerateMenuBar()
        {
            assert valid();

            // 새로운 메뉴 바를 생성하고 현재의 컨텐츠 리스트를
            // 추가한다.
            
            menuBar = new JMenuBar();
            ListIterator<Item> i = menuBarContents.listIterator( 0 );
            while( i.hasNext() )
                menuBar.add( ((Item) (i.next())).item );

            // 이전 메뉴 바를 새로운 메뉴 바로 교체한다.
            // setVisible 호출은 메뉴 바를 다시 그리도록 한다.
            // setVisible을 호출하지 않으면 메뉴 바를 다시 그리지
            // 않게 된다.

            menuFrame.setJMenuBar( menuBar );
            menuFrame.setVisible( true );
            return menuBar;
        }
    }

    private static class Debug
    {
        public interface Visitor
        {
            public void visit( JMenu e, int depth );
        }

        private static int traversalDepth = 1;

        public static void visitPostorder( MenuElement me, Visitor v )
        {
            // 만약 실제 타입이 (JMenu와 같은 JMenuItem 상속체가 아닌)
            // JMenuItem이라면 이는 리프 노드이며,
            // 자식을 갖고 있지 않다.

            if( me.getClass() != JMenuItem.class )
            {
                MenuElement[] contents = me.getSubElements();
                for( int i = 0; i < contents.length; ++i )
                {
                    if( contents[i].getClass() != JMenuItem.class )
                    {
                        ++traversalDepth;
                        visitPostorder( contents[i], v );
                        if( !(contents[i] instanceof JPopupMenu) )
                            v.visit( (JMenu) contents[i], traversalDepth);
                        --traversalDepth;
                    }
                }
            }
        }
    }
} 
