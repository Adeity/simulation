package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.App;
import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.controller.Controller;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.logging.*;

import static java.lang.Character.isDigit;

public class JFrameInit extends JFrame implements ActionListener{

    private volatile boolean needUpdate;

    private static final Logger LOG = Logger.getLogger(JFrameInit.class.getName());

    App app;

    JFrameSimulation jFrameSimulace;

    CardLayout cardLayout = new CardLayout(40, 30);
    FlowLayout flowLayout = new FlowLayout();

    NumberFormat integerFormat = NumberFormat.getIntegerInstance();
    NumberFormatter numberFormatter = new NumberFormatter(integerFormat);


    JFormattedTextField sizeField = new JFormattedTextField(numberFormatter);

    JFormattedTextField confSpawnMinAgeFox = new JFormattedTextField(numberFormatter);
    JFormattedTextField confSpawnMaxAgeFox = new JFormattedTextField(numberFormatter);
    JFormattedTextField confSpawnMinEnergyFox = new JFormattedTextField(numberFormatter);
    JFormattedTextField confSpawnMaxEnergyFox = new JFormattedTextField(numberFormatter);

    JFormattedTextField confOtherMinKillingAgeFox = new JFormattedTextField(numberFormatter);
    JFormattedTextField confOtherMinMatingAgeFox = new JFormattedTextField(numberFormatter);

    JFormattedTextField confSpawnMinAgeHare = new JFormattedTextField(numberFormatter);
    JFormattedTextField confSpawnMaxAgeHare = new JFormattedTextField(numberFormatter);
    JFormattedTextField confSpawnMinEnergyHare = new JFormattedTextField(numberFormatter);
    JFormattedTextField confSpawnMaxEnergyHare = new JFormattedTextField(numberFormatter);

    JFormattedTextField confOtherMinMatingAgeHare = new JFormattedTextField(numberFormatter);

    JTextField templateName = new JTextField();
    JTextField saveName = new JTextField();

    JLabel heading = new JLabel("Simulation application");
    JLabel welcome = new JLabel("Welcome");
    JLabel headingFromSize = new JLabel("Generate map from size");
    JLabel headingFromTemplate = new JLabel("Generate map from template");
    JLabel headingFromSave = new JLabel("Generate map from save file");
    JLabel headingParamsSpawn = new JLabel("Config of spawn attributes");
    JLabel headingParamsOther = new JLabel("Config of other attributes");
    JLabel headingFromTemplateError = new JLabel("Invalid template");
    JLabel headingFromSaveError = new JLabel("Invalid save");


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

    JButton btnRunFromParams = new JButton("Run");


    JPanel panelInit = new JPanel();
    JPanel panelSave = new JPanel();
    JPanel panelTemplate = new JPanel();
    JPanel panelSize = new JPanel();
    JPanel panelParameters = new JPanel();
    JPanel panelSaveError = new JPanel();
    JPanel panelTemplateError = new JPanel();

    private boolean fromTemplate;
    private boolean fromSave;
    private boolean fromSize;

    JTextField textField1;
    Container c;


    BoxLayout horizontalBox = new BoxLayout(c, BoxLayout.X_AXIS);
    BoxLayout verticalBox = new BoxLayout(c, BoxLayout.Y_AXIS);

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

        btnRunFromParams.setActionCommand("Run");
    }

    private void resetFromBooleans() {
        this.fromTemplate = false;
        this.fromSave = false;
        this.fromSize = false;
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
        btnRunFromParams.addActionListener(this);


        btnBackFromParam.addActionListener(new BackFromParamToSave());
    }

    public JFrameInit(App app) {
        this.app = app;
        LOG.setUseParentHandlers(false);
        Handler stdout = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        LOG.addHandler(stdout);
        stdout.setLevel(Level.FINEST);


        this.setTitle("Simulation start");

        //  integer formatter
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(null);

        //  this windows options
        this.setSize(new Dimension(370, 580));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.c = this.getContentPane();
        c.setLayout(cardLayout);

        this.backButtonsCommands();
        this.okButtonsCommands();
        this.setHeadingSizes();

        this.addActionListeners();
        this.initPanel();
        this.sizePanel();
        this.savePanel();
        this.templatePanel();
        this.saveErrorPanel();
        this.templateErrorPanel();
        this.paramsPanel();


        c.add(panelInit, "cardInit");
        c.add(panelSize, "cardSize");
        c.add(panelTemplate, "cardTemplate");
        c.add(panelSave, "cardSave");
        c.add(panelSaveError, "cardSaveError");
        c.add(panelTemplateError, "cardTemplateError");
        c.add(panelParameters, "cardParams");
    }

    private void setHeadingSizes() {
        this.heading.setFont(heading.getFont().deriveFont(20f));
        this.welcome.setFont(welcome.getFont().deriveFont(20f));
        this.headingFromSave.setFont(headingFromSave.getFont().deriveFont(20f));
        this.headingFromSize.setFont(headingFromSize.getFont().deriveFont(20f));
        this.headingFromTemplate.setFont(headingFromTemplate.getFont().deriveFont(20f));
        this.headingParamsSpawn.setFont(headingParamsSpawn.getFont().deriveFont(14f));
        this.headingParamsOther.setFont(headingParamsOther.getFont().deriveFont(14f));
        this.headingFromSaveError.setFont(headingFromSaveError.getFont().deriveFont(20f));
        this.headingFromTemplateError.setFont(headingFromTemplateError.getFont().deriveFont(20f));
    }

    private void saveErrorPanel() {
        panelSaveError.add(headingFromSaveError);
        panelSaveError.add(btnBackFromSaveErrorPanel);
    }

    private void templateErrorPanel() {
        panelTemplateError.add(headingFromTemplateError);
        panelTemplateError.add(btnBackFromTemplateErrorPanel);
    }

    private void initPanel() {
        panelInit = new JPanel();
        JPanel buttons = new JPanel();

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(btnFromSize);
        horizontalBox.add(Box.createRigidArea(new Dimension(5, 0)));
        horizontalBox.add(btnFromTemplate);
        horizontalBox.add(Box.createRigidArea(new Dimension(5, 0)));
        horizontalBox.add(btnFromSave);

        horizontalBox.setAlignmentX(0);

        panelInit.add(heading);
//        panelInit.add(welcome);
        panelInit.add(Box.createRigidArea(new Dimension(0, 20)));
        panelInit.add(new JLabel("Init map from: "));
        panelInit.add(Box.createRigidArea(new Dimension(0, 7)));

        panelInit.setAlignmentX(0);

        panelInit.add(horizontalBox);

//        panelInit.setAlignmentX(100);
//        panelInit.setLayout(new BoxLayout(panelInit, BoxLayout.PAGE_AXIS));

    }

    private void savePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        // All mandatory fields.
        // 1.
        JLabel hwCodeLabel = addMyLabelCell(0, "Save name: ", panel);
        saveName = addMyTextFieldCell(0, "", panel);


        Box horizontalBox = Box.createHorizontalBox();

        horizontalBox.add(btnBackFromSavePanel);
        horizontalBox.add(btnOkFromSavePanel);

        panelSave.add(headingFromSave);
        panelSave.add(panel);
        panelSave.add(horizontalBox);
        panelSave.setLayout(new FlowLayout());

//        panelSave.setLayout(new BoxLayout(panelSave, BoxLayout.PAGE_AXIS));
    }

    private void templatePanel() {
        panelSize.setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        // All mandatory fields.
        // 1.
        JLabel hwCodeLabel = addMyLabelCell(0, "Template name: ", panel);
        templateName = addMyTextFieldCell(0, "", panel);


        Box horizontalBox = Box.createHorizontalBox();

        horizontalBox.add(btnBackFromTemplatePanel);
        horizontalBox.add(btnOkFromTemplatePanel);

        panelTemplate.add(headingFromTemplate);
        panelTemplate.add(panel);
        panelTemplate.add(horizontalBox);
    }

    private void sizePanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        // All mandatory fields.
        // 1.
        JLabel sizeLabel = addMyLabelCell(0, "Size: ", panel);
        sizeField = addMyTextFieldCell(0, "", panel, true);
        sizeField.setValue(100);

        Box horizontalBox = Box.createHorizontalBox();

        horizontalBox.add(btnBackFromSizePanel);
        horizontalBox.add(btnOkFromSizePanel);

        panelSize.add(headingFromSize);
        panelSize.add(new JLabel("More than 100 is not recommended"));
        panelSize.add(panel);
        panelSize.add(horizontalBox);
        panelSize.setLayout(new FlowLayout());
    }

    private void paramsPanel() {
        JPanel bagSpawnFox = new JPanel();
        JPanel bagSpawnHare = new JPanel();
        JPanel bagOtherFox = new JPanel();
        JPanel bagOtherHare = new JPanel();

        bagSpawnFox.setLayout(new GridBagLayout());
        bagSpawnHare.setLayout(new GridBagLayout());
        bagOtherFox.setLayout(new GridBagLayout());
        bagOtherHare.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        // All mandatory fields.
        // 1.
        JLabel foxLabel = addMyLabelCell(0, "Fox: ", bagSpawnFox);

        JLabel foxMinAge = addMyLabelCell(1, "Min age: ", bagSpawnFox);
        confSpawnMinAgeFox = addMyTextFieldCell(1, "", bagSpawnFox, true);

        JLabel foxMaxAge = addMyLabelCell(2, "Max age: ", bagSpawnFox);
        confSpawnMaxAgeFox = addMyTextFieldCell(2, "", bagSpawnFox, true);

        JLabel foxMinEnergy = addMyLabelCell(3, "Min energy: ", bagSpawnFox);
        confSpawnMinEnergyFox = addMyTextFieldCell(3, "", bagSpawnFox, true);

        JLabel foxMaxEnergy = addMyLabelCell(4, "Max energy: ", bagSpawnFox);
        confSpawnMaxEnergyFox = addMyTextFieldCell(4, "", bagSpawnFox, true);

        JLabel hareLabel = addMyLabelCell(0, "Hare: ", bagSpawnHare);

        JLabel hareMinAge = addMyLabelCell(1, "Min age: ", bagSpawnHare);
        confSpawnMinAgeHare = addMyTextFieldCell(1, "", bagSpawnHare, true);

        JLabel hareMaxAge = addMyLabelCell(2, "Max age: ", bagSpawnHare);
        confSpawnMaxAgeHare = addMyTextFieldCell(2, "", bagSpawnHare, true);

        JLabel hareMinEnergy = addMyLabelCell(3, "Min energy: ", bagSpawnHare);
        confSpawnMinEnergyHare = addMyTextFieldCell(3, "", bagSpawnHare, true);

        JLabel hareMaxEnergy = addMyLabelCell(4, "Max energy: ", bagSpawnHare);
        confSpawnMaxEnergyHare = addMyTextFieldCell(4, "", bagSpawnHare, true);

        confSpawnMinAgeFox.setValue(CONF.FOX_INIT_MIN_AGE);
        confSpawnMaxAgeFox.setValue(CONF.FOX_INIT_MAX_AGE);
        confSpawnMinEnergyFox.setValue(CONF.FOX_INIT_MIN_ENERGY);
        confSpawnMaxEnergyFox.setValue(CONF.FOX_INIT_MAX_ENERGY);

        confSpawnMinAgeHare.setValue(CONF.HARE_INIT_MIN_AGE);
        confSpawnMaxAgeHare.setValue(CONF.HARE_INIT_MAX_AGE);
        confSpawnMinEnergyHare.setValue(CONF.HARE_INIT_MIN_ENERGY);
        confSpawnMaxEnergyHare.setValue(CONF.HARE_INIT_MAX_ENERGY);


        JLabel otherFoxLabel = addMyLabelCell(0, "Fox: ", bagOtherFox);

        JLabel otherFoxMinAgeMating = addMyLabelCell(1, "Min mating age: ", bagOtherFox);
        confOtherMinMatingAgeFox = addMyTextFieldCell(1, "", bagOtherFox, true);


        JLabel otherHareLabel = addMyLabelCell(0, "Hare: ", bagOtherHare);

        JLabel otherHareMinAgeMating = addMyLabelCell(1, "Min mating age: ", bagOtherHare);
        confOtherMinMatingAgeHare = addMyTextFieldCell(1, "", bagOtherHare, true);

        confOtherMinMatingAgeFox.setValue(CONF.FOX_MATING_MIN_AGE);

        confOtherMinMatingAgeHare.setValue(CONF.HARE_MATING_MIN_AGE);

        Box horizontalBox = Box.createHorizontalBox();

        horizontalBox.add(btnBackFromParam);
        horizontalBox.add(btnRunFromParams);

        panelParameters.add(headingParamsSpawn);
        panelParameters.add(bagSpawnFox);
        panelParameters.add(bagSpawnHare);
        panelParameters.add(headingParamsOther);
        panelParameters.add(bagOtherFox);
        panelParameters.add(bagOtherHare);
        panelParameters.add(horizontalBox);
        panelParameters.setLayout(new FlowLayout());
    }

    private void changeConfig() {
        CONF.FOX_INIT_MIN_AGE = Integer.parseInt(this.confSpawnMinAgeFox.getText());
        CONF.FOX_INIT_MAX_AGE = Integer.parseInt(this.confSpawnMaxAgeFox.getText());
        CONF.FOX_INIT_MIN_ENERGY = Integer.parseInt(this.confSpawnMinEnergyFox.getText());
        CONF.FOX_INIT_MAX_ENERGY = Integer.parseInt(this.confSpawnMaxEnergyFox.getText());

        CONF.HARE_INIT_MIN_AGE = Integer.parseInt(this.confSpawnMinAgeHare.getText());
        CONF.HARE_INIT_MAX_AGE = Integer.parseInt(this.confSpawnMaxAgeHare.getText());
        CONF.HARE_INIT_MIN_ENERGY = Integer.parseInt(this.confSpawnMinEnergyHare.getText());
        CONF.HARE_INIT_MAX_ENERGY = Integer.parseInt(this.confSpawnMaxEnergyHare.getText());


        CONF.FOX_MATING_MIN_AGE = Integer.parseInt(this.confOtherMinMatingAgeFox.getText());

        CONF.HARE_MATING_MIN_AGE = Integer.parseInt(this.confOtherMinMatingAgeHare.getText());
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

    /**
     * Transforms number formatted textfield to integer.
     * @return integer from textfield
     */
    private int getIntegerFromTextField(String textfield) {
        int res = Integer.parseInt(getParsableToInt(textfield));
        return res;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Run":
                c.setVisible(false);
                this.dispose();
                this.setVisible(false);
                changeConfig();
                if(this.fromSave) {
                    jFrameSimulace = new JFrameSimulation(this.app, this.saveName.getText());
                }
                else if(this.fromTemplate) {
                    jFrameSimulace = new JFrameSimulation(this.app, this.templateName.getText(), true);
                }
                else if(this.fromSize) {
                    jFrameSimulace = new JFrameSimulation(this.app, getIntegerFromTextField(this.sizeField.getText()));
                }
                jFrameSimulace.setVisible(true);
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
                resetFromBooleans();
                this.fromSize = true;
                removeActionListeners(btnBackFromParam);
                btnBackFromParam.addActionListener(new BackFromParamToSize());
                cardLayout.show(c, "cardParams");
                break;
            case "TEMPLATE_TO_PARAMS":
                resetFromBooleans();
                this.fromTemplate = true;
                removeActionListeners(btnBackFromParam);
                btnBackFromParam.addActionListener(new BackFromParamToTemplate());
                //check validity of template
                boolean templateFound = false;
                File f = new File(CONF.MAP_TEMPLATE_DIRECTORY);
                // Populates the array with names of files and directories
                String[] pathnames = f.list();
                if(pathnames != null) {
                    for(String pathname : pathnames) {
                        if(pathname.equals(templateName.getText())) {
                            LOG.info("Template found: " + pathname);
                            templateFound = true;
                            break;
                        }
                    }
                }
                if(templateFound) {
                    cardLayout.show(c, "cardParams");
                    break;
                }
                LOG.info("Template " + templateName.getText() + " not found");
                cardLayout.show(c, "cardTemplateError");
                break;
            case "SAVE_TO_PARAMS":
                resetFromBooleans();
                this.fromSave = true;
                removeActionListeners(btnBackFromParam);
                btnBackFromParam.addActionListener(new BackFromParamToSave());

                //check validity of template
                boolean saveFound = false;
                File fSave = new File(CONF.MAP_SAVES_DIRECTORY);
                // Populates the array with names of files and directories
                String[] pathnamesSaves = fSave.list();
                if(pathnamesSaves != null) {
                    for(String pathname : pathnamesSaves) {
                        if(pathname.equals(saveName.getText())) {
                            LOG.info("Save found: " + pathname);
                            saveFound = true;
                            break;
                        }
                    }
                }
                if(saveFound) {
                    cardLayout.show(c, "cardParams");
                    break;
                }
                LOG.info("Save " + saveName.getText() + " not found");
                cardLayout.show(c, "cardSaveError");
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

    public JLabel addMyLabelCell(int pos, String labelstr, JPanel panel) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = pos;
        JLabel label = new JLabel(labelstr);
        panel.add(label, gridBagConstraints);
        return label;
    }

    private JTextField addMyTextFieldCell(int pos, String text, JPanel panel) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.ipadx = 10;
        JTextField textField = new JTextField(text);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = pos;
        textField.setColumns(10);
        panel.add(textField, gridBagConstraints);
        return textField;
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
