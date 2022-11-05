/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Adeel
 */
public class Chess
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        int layoutSelected = LayoutSelector();
        // 选择开局大小
    }

    private static int LayoutSelector() {
        final int[] layoutSelected = {0};

        final JFrame jf = new JFrame("Chess Board Select");
        jf.setSize(380,400);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();

        final JRadioButton btn1_Classic = new JRadioButton("Classical Chess Board");
        btn1_Classic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutSelected[0] = Game.STD_LAYOUT;
            }
        });

        final JRadioButton btn2_XL = new JRadioButton("Enlarged Chess Board");
        btn2_XL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutSelected[0] = Game.XL_LAYOUT;
            }
        });

        final JRadioButton btn3_XL_TR = new JRadioButton("Enlarged Chess + Cannons (4 directions)");
        btn3_XL_TR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutSelected[0] = Game.XL_PLUS_CANNON;
            }
        });

        final JRadioButton btn4_XL_TR_8 = new JRadioButton("Enlarged Chess + Cannons (8 directions)");
        btn4_XL_TR_8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutSelected[0] = Game.XL_PLUS_CANNON_8D;
            }
        });

        final JRadioButton btn5_XL_TR_4m8c = new JRadioButton("Enlarged Chess + Cannons (4Dmove, 8Dcapture)");
        btn5_XL_TR_4m8c.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutSelected[0] = Game.XL_PLUS_CANNON_4m8c;
            }
        });

        final JButton btn_START = new JButton("START Chess Game");
        btn_START.addActionListener(new ActionListener() {
            @Override
            // 将会在按钮按下后执行下面的，实际需要执行完本大段程序，回到主线程后，由主线程调度执行JButton的监听后程序
            public void actionPerformed(ActionEvent e) {
                jf.dispose();
                Game game1 = new Game(layoutSelected[0]);
            }
        });

        ButtonGroup btnGrp = new ButtonGroup();
        btnGrp.add(btn1_Classic);
        btnGrp.add(btn2_XL);
        btnGrp.add(btn3_XL_TR);
        btnGrp.add(btn4_XL_TR_8);
        btnGrp.add(btn5_XL_TR_4m8c);

        btn3_XL_TR.setSelected(true);
        layoutSelected[0] = Game.XL_PLUS_CANNON;

        panel.add(btn1_Classic);
        panel.add(btn2_XL);
        panel.add(btn3_XL_TR);
        panel.add(btn4_XL_TR_8);
        panel.add(btn5_XL_TR_4m8c);
        panel.add(btn_START);

        jf.setContentPane(panel);
        jf.setVisible(true);

        return layoutSelected[0];
    }
}
