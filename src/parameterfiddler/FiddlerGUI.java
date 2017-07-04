/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parameterfiddler;

/**
 *
 * @author eulerschezahl
 */
public class FiddlerGUI extends javax.swing.JFrame {

    /**
     * Creates new form FiddlerGUI
     */
    public FiddlerGUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        labelTester = new javax.swing.JLabel();
        textboxTester = new javax.swing.JTextField();
        jSeparator8 = new javax.swing.JSeparator();
        labelReferee = new javax.swing.JLabel();
        textboxReferee = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        labelBot = new javax.swing.JLabel();
        textboxBot = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JSeparator();
        labelParameters = new javax.swing.JLabel();
        textboxParameters = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        labelDelta = new javax.swing.JLabel();
        textboxDelta = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        labelRuns = new javax.swing.JLabel();
        textboxRuns = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        labelThreads = new javax.swing.JLabel();
        textboxThreads = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        buttonRun = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        textareaParameterchanges = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        textareaBrutaltester = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ParameterFiddler");

        labelTester.setText("brutaltester:");
        jPanel1.add(labelTester);

        textboxTester.setColumns(10);
        textboxTester.setText("cg-brutaltester.jar");
        jPanel1.add(textboxTester);

        jSeparator8.setPreferredSize(new java.awt.Dimension(5, 0));
        jPanel1.add(jSeparator8);

        labelReferee.setText("referee:");
        jPanel1.add(labelReferee);

        textboxReferee.setColumns(10);
        textboxReferee.setText("java -jar cg-c4l.jar");
        jPanel1.add(textboxReferee);

        jSeparator1.setPreferredSize(new java.awt.Dimension(5, 0));
        jPanel1.add(jSeparator1);

        labelBot.setText("bot:");
        jPanel1.add(labelBot);

        textboxBot.setColumns(10);
        jPanel1.add(textboxBot);

        jSeparator5.setPreferredSize(new java.awt.Dimension(5, 0));
        jPanel1.add(jSeparator5);

        labelParameters.setText("parameter file:");
        jPanel1.add(labelParameters);

        textboxParameters.setColumns(10);
        textboxParameters.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(textboxParameters);

        jSeparator2.setPreferredSize(new java.awt.Dimension(5, 0));
        jPanel1.add(jSeparator2);

        labelDelta.setText("delta:");
        jPanel1.add(labelDelta);

        textboxDelta.setColumns(3);
        textboxDelta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textboxDelta.setText("0.3");
        jPanel1.add(textboxDelta);

        jSeparator3.setPreferredSize(new java.awt.Dimension(5, 0));
        jPanel1.add(jSeparator3);

        labelRuns.setText("runs:");
        jPanel1.add(labelRuns);

        textboxRuns.setColumns(3);
        textboxRuns.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textboxRuns.setText("1000");
        jPanel1.add(textboxRuns);

        jSeparator4.setPreferredSize(new java.awt.Dimension(5, 0));
        jPanel1.add(jSeparator4);

        labelThreads.setText("threads:");
        jPanel1.add(labelThreads);

        textboxThreads.setColumns(3);
        textboxThreads.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textboxThreads.setText("4");
        jPanel1.add(textboxThreads);

        jSeparator7.setPreferredSize(new java.awt.Dimension(10, 0));
        jPanel1.add(jSeparator7);

        buttonRun.setText("RUN");
        buttonRun.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel1.add(buttonRun);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Parameter", "old value", "new value", "testing"
            }
        ));
        jTable1.setEnabled(false);
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        textareaParameterchanges.setColumns(20);
        textareaParameterchanges.setRows(5);
        jScrollPane2.setViewportView(textareaParameterchanges);

        jTabbedPane1.addTab("parameter changes", jScrollPane2);

        textareaBrutaltester.setColumns(20);
        textareaBrutaltester.setRows(5);
        jScrollPane3.setViewportView(textareaBrutaltester);

        jTabbedPane1.addTab("brutaltester", jScrollPane3);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.SOUTH);

        jLabel1.setText("opponents");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel1)
                .addContainerGap(465, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.LINE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FiddlerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FiddlerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FiddlerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FiddlerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FiddlerGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonRun;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel labelBot;
    private javax.swing.JLabel labelDelta;
    private javax.swing.JLabel labelParameters;
    private javax.swing.JLabel labelReferee;
    private javax.swing.JLabel labelRuns;
    private javax.swing.JLabel labelTester;
    private javax.swing.JLabel labelThreads;
    private javax.swing.JTextArea textareaBrutaltester;
    private javax.swing.JTextArea textareaParameterchanges;
    private javax.swing.JTextField textboxBot;
    private javax.swing.JTextField textboxDelta;
    private javax.swing.JTextField textboxParameters;
    private javax.swing.JTextField textboxReferee;
    private javax.swing.JTextField textboxRuns;
    private javax.swing.JTextField textboxTester;
    private javax.swing.JTextField textboxThreads;
    // End of variables declaration//GEN-END:variables
}