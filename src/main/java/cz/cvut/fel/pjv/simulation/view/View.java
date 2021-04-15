package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.App;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.controller.Controller;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

public class View extends JFrame implements ActionListener, Observer{

    private Simulation simulation;

    private volatile boolean needUpdate;

    NumberFormat integerFormat = NumberFormat.getIntegerInstance();
    NumberFormatter numberFormatter = new NumberFormatter(integerFormat);


    JFormattedTextField sizeField = new JFormattedTextField(numberFormatter);
    JFormattedTextField confHareEnergy = new JFormattedTextField(numberFormatter);
    JFormattedTextField confFoxEnergy = new JFormattedTextField(numberFormatter);



    JButton btnFromSize = new JButton("Size");
    JButton btnFromTemplate = new JButton("Template");
    JButton btnFromSave = new JButton("Save");
    JButton btnRun = new JButton("Run");


    JButton btnBackFromTemplatePanel = new JButton("Back");
    JButton btnBackFromTemplateErrorPanel = new JButton("Back");
    JButton btnBackFromSavePanel = new JButton("Back");
    JButton btnBackFromSaveErrorPanel = new JButton("Back");
    JButton btnBackFromSizePanel = new JButton("Back");
    JButton btnBackFromParam = new JButton("Back");

    JButton btnOkFromTemplatePanel = new JButton("Ok");
    JButton btnOkFromSavePanel = new JButton("Ok");
    JButton btnOkFromSizePanel = new JButton("Ok");


    JPanel panelInit = new JPanel();
    JPanel panelSave = new JPanel();
    JPanel panelTemplate = new JPanel();
    JPanel panelSize = new JPanel();
    JPanel panelParameters = new JPanel();

    private boolean fromTemplate;
    private boolean fromSave;
    private boolean fromSize;

    JTextField textField1;
    CardLayout cardLayout = new CardLayout(40, 30);
    Container c;
    MapComponent map;
    JButton jb1, jb2, jb3;
    Controller controller;

    private void backButtonsCommands() {
        btnBackFromTemplatePanel.setActionCommand("FROM_TEMPLATE_PANEL");
        btnBackFromTemplateErrorPanel.setActionCommand("FROM_TEMPLATE_ERROR_PANEL");

        btnBackFromSavePanel.setActionCommand("FROM_SAVE_PANEL");
        btnBackFromSaveErrorPanel.setActionCommand("FROM_SAVE_ERROR_PANEL");

        btnBackFromSizePanel.setActionCommand("FROM_SIZE_PANEL");
    }

    private void okButtonsCommands() {
        btnOkFromTemplatePanel.setActionCommand("TEMPLATE_TO_PARAMS");
        btnOkFromSavePanel.setActionCommand("SAVE_TO_PARAMS");
        btnOkFromSizePanel.setActionCommand("SIZE_TO_PARAMS");
    }

    private void resetFroms() {
        this.fromTemplate = false;
        this.fromSave = false;
        this.fromSize = false;
    }

    public View(App app) {
        this.setTitle("Simulace");
        this.controller = app.getController();
        this.simulation = app.getSimulation();

        //  integer formatter
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(null);

        //  this windows options
        this.setSize(new Dimension(540, 720));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.c = this.getContentPane();
        c.setLayout(cardLayout);

        this.backButtonsCommands();
        this.okButtonsCommands();

        this.addActionListeners();
        this.initPanel();
        this.sizePanel();
        this.savePanel();
        this.templatePanel();
        this.paramsPanel();


        c.add(panelInit, "cardInit");
        c.add(panelSize, "cardSize");
        c.add(panelTemplate, "cardTemplate");
        c.add(panelSave, "cardSave");
        c.add(panelParameters, "cardParams");

    }

    private void initPanel() {
        panelInit = new JPanel();
        panelInit.add(new JLabel("Init map from: "));
        panelInit.add(btnFromSize);
        panelInit.add(btnFromTemplate);
        panelInit.add(btnFromSave);
        panelInit.setLayout(new FlowLayout());
    }

    private void savePanel() {
        panelSave.add(new JLabel("Enter save name: "));
        panelSave.add(btnBackFromSavePanel);
        panelSave.add(btnOkFromSavePanel);
        panelSave.setLayout(new FlowLayout());
    }

    private void templatePanel() {
        panelTemplate.add(new JLabel("Enter template name: "));
        panelTemplate.add(btnOkFromTemplatePanel);
        panelTemplate.add(btnBackFromTemplatePanel);
    }

    private void sizePanel() {
        panelSize.add(new JLabel("Set size: "));
        sizeField.setPreferredSize(new Dimension(200, 200));
        panelSize.add(sizeField);
        panelSize.add(btnOkFromSizePanel);
        panelSize.add(btnBackFromSizePanel);
        panelSize.setLayout(new FlowLayout());
    }

    private void paramsPanel() {
        JLabel paramLabel = new JLabel("Parameters: ");
        panelParameters.add(paramLabel);
        panelParameters.add(btnBackFromParam);

    }

    private void addActionListeners() {
        btnFromSize.addActionListener(this);
        btnFromTemplate.addActionListener(this);
        btnFromSave.addActionListener(this);
        btnRun.addActionListener(this);
        btnBackFromTemplatePanel.addActionListener(this);
        btnBackFromTemplateErrorPanel.addActionListener(this);
        btnBackFromSavePanel.addActionListener(this);
        btnBackFromSaveErrorPanel.addActionListener(this);
        btnBackFromSizePanel.addActionListener(this);

        btnOkFromSavePanel.addActionListener(this);
        btnOkFromSizePanel.addActionListener(this);
        btnOkFromTemplatePanel.addActionListener(this);


        btnBackFromParam.addActionListener(new BackFromParamToSave());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Next":
                this.controller.simulateDay();
                map.repaint();
                this.repaint();
                break;
            case "Run":
                this.controller.run("map1.txt");
                this.map = new MapComponent(this.simulation.map);
                this.remove(jb2);
                this.add(map, BorderLayout.NORTH);
                break;
            case "Reset":
                break;
            case "Size":
                cardLayout.show(c, "cardSize");
                break;
            case "Template":
                cardLayout.show(c, "cardTemplate");
                break;
            case "Save":
                cardLayout.show(c, "cardSave");
                break;
            case "FROM_TEMPLATE_PANEL":
                cardLayout.show(c, "cardInit");
                break;
            case "FROM_TEMPLATE_ERROR_PANEL":
                cardLayout.show(c, "cardTemplate");
                break;
            case "FROM_SAVE_PANEL":
                cardLayout.show(c, "cardInit");
                break;
            case "FROM_SAVE_ERROR_PANEL":
                cardLayout.show(c, "cardSave");
                break;
            case "FROM_SIZE_PANEL":
                cardLayout.show(c, "cardInit");
                break;

            case "SIZE_TO_PARAMS":
                removeActionListeners(btnBackFromParam);
                btnBackFromParam.addActionListener(new BackFromParamToSize());
                cardLayout.show(c, "cardParams");
                break;
            case "TEMPLATE_TO_PARAMS":
                removeActionListeners(btnBackFromParam);
                btnBackFromParam.addActionListener(new BackFromParamToTemplate());
                cardLayout.show(c, "cardParams");
                break;
            case "SAVE_TO_PARAMS":
                removeActionListeners(btnBackFromParam);
                btnBackFromParam.addActionListener(new BackFromParamToSave());
                cardLayout.show(c, "cardParams");
                break;

            case "FROM_PARAM_TO_TEMPLATE_PANEL":
                cardLayout.show(c, "cardTemplate");
                break;
            case "FROM_PARAM_TO_SAVE_PANEL":
                cardLayout.show(c, "cardSave");
                break;
            case "FROM_PARAM_TO_SIZE_PANEL":
                cardLayout.show(c, "cardSize");
                break;
        }

        for (Component c : this.getComponents()) {
            c.repaint();
        }
        this.repaint();
    }

    private void removeActionListeners(JButton btn) {
        for (ActionListener actionListener : btn.getActionListeners()) {
            btn.removeActionListener(actionListener);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        needUpdate = true;
    }

    private class BackFromParamToTemplate implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            cardLayout.show(c, "cardTemplate");
        }
    }
    private class BackFromParamToSize implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            cardLayout.show(c, "cardSize");
        }
    }
    private class BackFromParamToSave implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            cardLayout.show(c, "cardSave");
        }
    }
}
