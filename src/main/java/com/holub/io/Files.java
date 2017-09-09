package com.holub.io;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter; // disambiguate from java.io version

public class Files
{
    /*
     * 파일 선택기를 보여주고, 사용자가 선택한 파일을 반한한다.
     *
     * @param extension 찾고자 하는 파일 확장자 모두
     * 사용하고 싶다면, null을 넘기면 된다.
     *
     * @param description 확장자의 의미를 설명
     * "extention"이 null이면 사용되지 않는다.
     *
     * @param selectButtonText 선택 버튼의 "Open"
     * 문자열을 대체한다.
     *
     * @param startHere 파일 찾기를 시작할 디렉토리의 이름
     *
     * @return 선택된 파일을 나타내는 {@link File}
     *
     * @throws FileNotFoundException 사용자가
     * 파일을 선택하지 않았을 때 null 값 대신 예외를 던지면, 다음과
     * 같은 방식으로 프로그램을 사용하기 편리한다. <PRE>
     * FileInputStream in = new
     *     FileInputStream( Files.userSelected(
     *         ".", ".txt", "Text File", "Open" ));
     * </PRE>
     */

    public static File userSelected( final String startHere, final String extension,
            final String description, final String selectButtonText ) throws FileNotFoundException
    {
        FileFilter filter = new FileFilter()
        {
            public boolean accept( File f )
            {
                return f.isDirectory() || (extension != null && f.getName().endsWith( extension ));
            }

            public String getDescription()
            {
                return description;
            }
        };

        JFileChooser chooser = new JFileChooser( startHere );
        chooser.setFileFilter( filter );

        int result = chooser.showDialog( null, selectButtonText );
        if( result == JFileChooser.APPROVE_OPTION )
            return chooser.getSelectedFile();

        throw new FileNotFoundException( "No file selected by user" );
    }

    static class Test
    {
        public static void main( String[] args )
        {
            try
            {
                File f = Files.userSelected( ".", ".test", "Test File", "Select!" );
                System.out.println( "Selected " + f.getName() );
            } catch( FileNotFoundException e )
            {
                System.out.println( "No file selected" );
            }
            System.exit( 0 ); // Required to stop AWT thread & shut
                                // down.
        }
    }
}
