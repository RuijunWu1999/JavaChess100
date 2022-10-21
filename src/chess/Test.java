package chess;/*
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        // Progress Bar Test
        JFrame myFrame = new JFrame("myJfTitle");
        myFrame.setLayout(new BorderLayout());
        JButton myButton = new JButton("Click me");
        myButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JProgressBar myBar = new JProgressBar();
        LayoutManager overlay = new OverlayLayout(myBar);
        myBar.setLayout(overlay);
        myBar.setValue(50);
        myBar.add(myButton);
        myFrame.add(myBar, BorderLayout.CENTER);
        myFrame.pack();
        myFrame.setSize(new Dimension(300,100));
        myFrame.setVisible(true);
    }
}
*/

import java.awt.BorderLayout;

import java.awt.Color;

import java.awt.FlowLayout;

import java.awt.event.ComponentAdapter;

import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import javax.swing.JLabel;

import javax.swing.JPanel;

import javax.swing.border.CompoundBorder;

import javax.swing.border.EmptyBorder;

import javax.swing.border.LineBorder;

public class Test {

    public static void main(String[] args) {

        JPanel statusBar1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar1.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY),
                new EmptyBorder(4, 4, 4, 4)));
        final JLabel status1 = new JLabel();
        statusBar1.add(status1);
        statusBar1.setBackground(Color.cyan);

        JPanel statusBar2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar2.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY),
                new EmptyBorder(4, 4, 4, 4)));
        final JLabel status2 = new JLabel();
        statusBar2.add(status2);
        statusBar2.setBackground(Color.pink);


        JLabel content = new JLabel("Content in the middle");
        content.setHorizontalAlignment(JLabel.CENTER);

        final JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(content);
        frame.add(statusBar2, BorderLayout.SOUTH);
        frame.add(statusBar1, BorderLayout.NORTH);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                status2.setText(frame.getWidth() + "x" + frame.getHeight());
                status1.setText(frame.getWidth() + "x" + frame.getHeight());
            }

        });

        frame.setBounds(20,20,200,200);
        frame.setVisible(true);

    }

}
/*
        版权声明：本文为CSDN博主「白泽之水」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
        原文链接：https://blog.csdn.net/weixin_30744725/article/details/114074909

 */