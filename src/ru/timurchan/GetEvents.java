package ru.timurchan;

import ru.timurchan.vkdata.VkDataCollector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Timur on 29.01.2017.
 */
public class GetEvents {
    private JButton buttonFriends;
    private JPanel panelMain;
    private JButton buttonEvents;
    private JButton buttonSaveFriends;
    private JButton buttonLoadFriends;
    private JButton buttonStopGettngFriinds;

    private VkDataCollector vkDataCollector = new VkDataCollector();

    public GetEvents() {
        buttonFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.collectFriends();
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
            }
        });
        buttonEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.collectEvents();
            }
        });
        buttonStopGettngFriinds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vkDataCollector.stopGettingFriends();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GetEvents");
        frame.setContentPane(new GetEvents().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
