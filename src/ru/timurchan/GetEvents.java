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
    private JTextField tfMeetingsPackCount;
    private JLabel labelFEProcessed;
    private JTextField tfInitialFriendId;
    private JLabel labelFEProcessedCount;
    private JButton buttonLoadProcessedFriends;
    private JLabel labelPreviousProcessedFriends;
    private JRadioButton radioFriendsLevel2;
    private JRadioButton radioFriendsLevel1;

    static private VkDataCollector vkDataCollector = new VkDataCollector();


    public static void main(String[] args) {
        JFrame frame = new JFrame("GetEvents");
        GetEvents getEventInstance = new GetEvents();
        frame.setContentPane(getEventInstance.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        addWindowListener(frame);
        frame.setSize(750, 300);
        vkDataCollector.setWindow(getEventInstance);
    }

    public GetEvents() {
        buttonGetFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = tfInitialFriendId.getText();
                try {
                    Integer tmp = Integer.valueOf(str); // initial friend id have to be a number
                    vkDataCollector.setInitilFriendId(str);
                } catch (NumberFormatException e_number) {
                    System.out.println("Wrong input value in tfMeetingsPackCount field. String is " + str);
                }
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
                buttonStopGettingEvents.setEnabled(false);
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
                buttonStopGettingEvents.setEnabled(false);
                buttonStopGettngFriinds.setEnabled(false);
            }
        });

        buttonGetEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.collectEvents();
                buttonGetFriends.setEnabled(false);
                buttonStopGettngFriinds.setEnabled(false);
                buttonSaveFriends.setEnabled(false);
                buttonLoadFriends.setEnabled(false);
                buttonGetEvents.setEnabled(false);
                buttonStopGettingEvents.setEnabled(true);
                tfMeetingsPackCount.setEnabled(false);
            }
        });
        buttonStopGettingEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.stopGettingEvents();
                buttonGetFriends.setEnabled(true);
                buttonStopGettngFriinds.setEnabled(false);
                buttonSaveFriends.setEnabled(true);
                buttonLoadFriends.setEnabled(true);
                buttonGetEvents.setEnabled(true);
                buttonStopGettingEvents.setEnabled(false);
                tfMeetingsPackCount.setEnabled(true);
            }
        });

        buttonStopGettngFriinds.setEnabled(false);
        buttonSaveFriends.setEnabled(false);
        buttonGetEvents.setEnabled(false);
        buttonStopGettingEvents.setEnabled(false);
        tfMeetingsPackCount.setText("10");
        labelFEProcessed.setText("");
        tfInitialFriendId.setText("69822");
        buttonLoadProcessedFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.loadProcessedFriends();
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(radioFriendsLevel1);
        group.add(radioFriendsLevel2);
        setFriendsLevel2();
    }

    public void setFriendsCount(int count) {
        labelFriendCount.setText(String.valueOf(count));
    }

    public void setFEProcessedFriendsPercent(int count) {
        labelFEProcessed.setText(String.valueOf(count));
    }

    public void setFriendsLevel2() {
        radioFriendsLevel2.setSelected(true);
        radioFriendsLevel1.setSelected(false);
    }

    public boolean isFriendsLevel2() {
        return radioFriendsLevel2.isSelected();
    }

    public void setFEProcessedFriendsCount(int count) {
        labelFEProcessedCount.setText(String.valueOf(count));
    }

    public void setPreviousProcessedFriendsCount(int countFriends, int countEvents) {
        String text = String.valueOf(countFriends) + " friends & " +
                String.valueOf(countEvents) + " events";
        labelPreviousProcessedFriends.setText(text);
    }

    public void setEventsCount(int count) {
        labelEventCount.setText(String.valueOf(count));
        String str = tfMeetingsPackCount.getText();
        try {
            Integer packCount = Integer.valueOf(str);
            vkDataCollector.setMeetingsPackCount(packCount);
        } catch (NumberFormatException e) {
            System.out.println("Wrong input value in tfMeetingsPackCount field. String is " + str);
        }
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
