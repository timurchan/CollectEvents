package ru.timurchan;

import ru.timurchan.vkdata.VkDataCollector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by Timur on 29.01.2017.
 */
public class GetEvents {
    private JPanel panelMain;

    private JButton buttonGetFriends;
    private JButton buttonStopGettngFriinds;
    private JLabel labelFriendCount;

    private JButton buttonSaveFriends;
    private JButton buttonLoadFriends;

    private JButton buttonGetEvents;
    private JButton buttonStopGettingEvents;
    private JLabel labelEventCount;

    static private VkDataCollector vkDataCollector = new VkDataCollector();


    public static void main(String[] args) {
        JFrame frame = new JFrame("GetEvents");
        GetEvents getEventInstance = new GetEvents();
        frame.setContentPane(getEventInstance.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        addWindowListener(frame);
        frame.setSize(500, 200);
        vkDataCollector.setWindow(getEventInstance);
    }

    public GetEvents() {
        buttonGetFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.collectFriends();
                buttonGetFriends.setEnabled(false);
                buttonStopGettngFriinds.setEnabled(true);
                buttonSaveFriends.setEnabled(false);
                buttonLoadFriends.setEnabled(false);
                buttonGetEvents.setEnabled(false);
                buttonStopGettingEvents.setEnabled(false);
            }
        });
        buttonStopGettngFriinds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.stopGettingFriends();
                buttonGetFriends.setEnabled(true);
                buttonStopGettngFriinds.setEnabled(false);
                buttonSaveFriends.setEnabled(true);
                buttonLoadFriends.setEnabled(true);
                buttonGetEvents.setEnabled(true);
                buttonStopGettingEvents.setEnabled(true);
            }
        });

        buttonSaveFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.saveFriends();
            }
        });
        buttonLoadFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.loadFriends();
                buttonGetEvents.setEnabled(true);
                buttonStopGettingEvents.setEnabled(true);
            }
        });

        buttonGetEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.collectEvents();
                buttonGetEvents.setEnabled(false);
            }
        });
        buttonStopGettingEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.stopGettingEvents();
            }
        });

        buttonStopGettngFriinds.setEnabled(false);
        buttonSaveFriends.setEnabled(false);
        buttonGetEvents.setEnabled(false);
        buttonStopGettingEvents.setEnabled(false);

    }

    public void setFriendsCount(int count) {
        labelFriendCount.setText(String.valueOf(count));
    }

    public void setEventsCount(int count) {
        labelEventCount.setText(String.valueOf(count));
    }

    static private void addWindowListener(JFrame frame) {
        frame.addWindowListener(new WindowListener() {

            @Override
            public void windowClosing(WindowEvent e) {
//                if (JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to quit?", "Confirm exit.", JOptionPane.OK_OPTION, 0, new ImageIcon("")) != 0) {
//                    return;
//                }
                vkDataCollector.stopAll();
                System.exit(-1);
            }

            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}

        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
