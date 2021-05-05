package cz.cvut.fel.pjv.simulation.network.server.view;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;
import cz.cvut.fel.pjv.simulation.utils.Utilities;
import cz.cvut.fel.pjv.simulation.view.JFrameInit;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.logging.Logger;

import static java.lang.Character.isDigit;

public class JFrameServerInit extends JFrame implements ActionListener {
    private static final Logger LOG = Logger.getLogger(JFrameInit.class.getName());

    SimulationServer simulationServer;

    NumberFormat integerFormat = NumberFormat.getIntegerInstance();
    NumberFormatter numberFormatter = new NumberFormatter(integerFormat);

    JButton btnOneClients = new JButton("1");
    JButton btnTwoClients = new JButton("2");
    JButton btnThreeClients = new JButton("3");
    JButton btnFourClients = new JButton("4");

    JButton btnBackFromMapPanel = new JButton("Back");
    JButton btnOkFromMapPanel = new JButton("Ok");

    JButton btnGoFromWaitPanel = new JButton("Go");



    JLabel heading = new JLabel("Max num of players: ");
    JLabel sizeOfEachMap = new JLabel("Size of each map: ");

    JLabel waitRoomConnectedClients = new JLabel("0/4");

    JFormattedTextField sizeOfEachMapField = new JFormattedTextField(numberFormatter);
    JFormattedTextField port = new JFormattedTextField(numberFormatter);

    JLabel currentNumOfConnectedClients;

    JPanel panelInit = new JPanel();
    JPanel panelMapSize = new JPanel();
    JPanel panelWaitRoom = new JPanel();

    CardLayout cardLayout = new CardLayout(40, 30);
    Container c;

    private int numOfClients;



    JFrameServerInit(SimulationServer simulationServer) {
        this.simulationServer = simulationServer;
        addActionListeners();
        setActionCommands();
        this.setTitle("Simulation server");
        //  integer formatter
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(null);

        this.setSize(new Dimension(370, 580));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.c = this.getContentPane();
        c.setLayout(cardLayout);

        this.initPanel();
        this.mapSizePanel();
        this.waitRoomPanel();

        c.add(panelInit, "cardInit");
        c.add(panelMapSize, "cardMapSize");
        c.add(panelWaitRoom, "cardWaitRoom");
    }

    private void initPanel() {
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(btnOneClients);
        horizontalBox.add(Box.createRigidArea(new Dimension(5, 0)));
        horizontalBox.add(btnTwoClients);
        horizontalBox.add(Box.createRigidArea(new Dimension(5, 0)));
        horizontalBox.add(btnThreeClients);
        horizontalBox.add(Box.createRigidArea(new Dimension(5, 0)));
        horizontalBox.add(btnFourClients);

        JLabel headinginit = new JLabel("Simulation server");
        headinginit.setFont(headinginit.getFont().deriveFont(20f));
        panelInit.add(headinginit);
        panelInit.add(heading);
        panelInit.add(horizontalBox);
    }

    private void mapSizePanel() {
        JPanel bag = new JPanel();
        bag.setLayout(new GridBagLayout());

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(btnBackFromMapPanel);
        horizontalBox.add(Box.createRigidArea(new Dimension(5, 0)));
        horizontalBox.add(btnOkFromMapPanel);

        sizeOfEachMap = addMyLabelCell(0, "Size of each map: ", bag);
        sizeOfEachMapField = addMyTextFieldCell(0, "", bag, true);

        JLabel portLabel = addMyLabelCell(1, "Port: ", bag);
        port = addMyTextFieldCell(1, "", bag, true);

        sizeOfEachMapField.setValue(20);
        port.setValue(8888);

        JLabel mapSizeHeading = new JLabel("Choose size of each map");
        mapSizeHeading.setFont(mapSizeHeading.getFont().deriveFont(20f));

        panelMapSize.add(mapSizeHeading);
        panelMapSize.add(bag);
        panelMapSize.add(horizontalBox);
        panelMapSize.setLayout(new FlowLayout());
    }

    private void waitRoomPanel() {
        JLabel waitRoomLabel = new JLabel("Waiting for clients: ");
//        waitRoomLabel.setFont(waitRoomLabel.getFont().deriveFont(20f));

        waitRoomConnectedClients.setText(simulationServer.getNumOfConnectedClients() + " / " + simulationServer.getMaxNumOfClients());

        panelWaitRoom.add(waitRoomLabel);
        panelWaitRoom.add(waitRoomConnectedClients);
        panelWaitRoom.add(btnGoFromWaitPanel);
        panelWaitRoom.setLayout(new FlowLayout());
    }

    public void updateWaitRoomNumbers() {
        waitRoomConnectedClients.setText(simulationServer.getNumOfConnectedClients() + " / " + simulationServer.getMaxNumOfClients());
    }

    public void repaint() {
        super.repaint();
        updateWaitRoomNumbers();
        for (Component c : this.getComponents()) {
            c.repaint();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "ONE_CLIENT":
                this.simulationServer.setMaxNumOfClients(1);
                cardLayout.show(c, "cardMapSize");
                break;
            case "TWO_CLIENTS":
                this.simulationServer.setMaxNumOfClients(2);
                cardLayout.show(c, "cardMapSize");
                break;
            case "THREE_CLIENTS":
                this.simulationServer.setMaxNumOfClients(3);
                cardLayout.show(c, "cardMapSize");
                break;
            case "FOUR_CLIENTS":
                this.simulationServer.setMaxNumOfClients(4);
                cardLayout.show(c, "cardMapSize");
                break;
            case "BACK_FROM_MAP_SIZE":
                cardLayout.show(c, "cardInit");
                break;
            case "OK_FROM_MAP_SIZE":
                if (sizeOfEachMapField.getText().equals("") || port.getText().equals("")) {
                    break;
                }
                int eachLocalMapsize = getIntegerFromTextField(sizeOfEachMapField.getText());
                int port = getIntegerFromTextField(this.port.getText());
                this.simulationServer.setLocalMapSize(eachLocalMapsize);
                this.simulationServer.setPort(port);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        simulationServer.listen();
                    }
                });
                t.start();
                LOG.info("Simulation server started listening for clients on port: " + simulationServer.getPort());
                LOG.info("Each local map size was set to: " + eachLocalMapsize);
                cardLayout.show(c, "cardWaitRoom");
                break;
            case "GO":
                if (simulationServer.getNumOfConnectedClients() == 0) {
                    break;
                }
                simulationServer.setRunning(true);
                this.simulationServer.getView().openJFrameServerSimulation(simulationServer);
                simulationServer.stopInitStartSimulating();
                this.setVisible(false);
                this.dispose();
                break;
        }

        for (Component c : this.getComponents()) {
            c.repaint();
        }
        this.repaint();
    }

    /**
     * Transforms number formatted textfield to integer.
     * @return integer from textfield
     */
    private int getIntegerFromTextField(String textfield) {
        int res = Integer.parseInt(getParsableToInt(textfield));
        return res;
    }

    /**
     * Number formatted text fields can produce texts such as 100,000. These give a NumberFormatException.
     * This method transforms such texts into a format parasble to integer through Integere.parseInt() method
     * @return return String in format parsable to integer.
     */
    private String getParsableToInt(String numberToTransform) {
        String res = "";
        for (int i = 0; i < numberToTransform.length(); i++) {
            char c = numberToTransform.charAt(i);
            if (isDigit(c)){
                res += c;
            }
        }
        return res;
    }

    private void setActionCommands() {
        btnOneClients.setActionCommand("ONE_CLIENT");
        btnTwoClients.setActionCommand("TWO_CLIENTS");
        btnThreeClients.setActionCommand("THREE_CLIENTS");
        btnFourClients.setActionCommand("FOUR_CLIENTS");
        btnBackFromMapPanel.setActionCommand("BACK_FROM_MAP_SIZE");
        btnOkFromMapPanel.setActionCommand("OK_FROM_MAP_SIZE");
        btnGoFromWaitPanel.setActionCommand("GO");
        LOG.info("Action commands were added to buttons");
    }

    private void addActionListeners() {
        btnOneClients.addActionListener(this);
        btnTwoClients.addActionListener(this);
        btnThreeClients.addActionListener(this);
        btnFourClients.addActionListener(this);
        btnBackFromMapPanel.addActionListener(this);
        btnOkFromMapPanel.addActionListener(this);
        btnGoFromWaitPanel.addActionListener(this);
        LOG.info("Action listeners were added to buttons");
    }

    public JLabel addMyLabelCell(int pos, String labelstr, JPanel panel) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = pos;
        JLabel label = new JLabel(labelstr);
        panel.add(label, gridBagConstraints);
        return label;
    }

    private JFormattedTextField addMyTextFieldCell(int pos, String text, JPanel panel, boolean t) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.ipadx = 10;

        NumberFormat longFormat = NumberFormat.getIntegerInstance();
        NumberFormatter numberFormatter = new NumberFormatter(longFormat);
        numberFormatter.setValueClass(Integer.class); //optional, ensures you will always get a long value
        numberFormatter.setAllowsInvalid(false); //this is the key!!
        numberFormatter.setMinimum(0); //Optional

        JFormattedTextField field = new JFormattedTextField(numberFormatter);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = pos;
        field.setColumns(10);
        panel.add(field, gridBagConstraints);
        return field;
    }
}
