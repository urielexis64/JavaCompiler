import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends JFrame implements KeyListener, ActionListener, UndoableEditListener {

    private JTextArea txtConsola, txtLineas, txtMensaje;
    private int lineas;
    private JScrollPane scrollLineas, scrollConsola, scrollMensaje;
    private ArrayList<String> identificadores;
    private JButton btnCompilar, btnAbrirArchivo;
    private static String ruta;
    private UndoManager undoManager;
    private JTable tablaSimbolos;
    public static ModeloTabla modelo;
    private JScrollPane scrollTable;

    public Main() {
        lineas = 1;
        identificadores = new ArrayList<>();
        hazInterfaz();
    }

    private void hazInterfaz() {
        setTitle("Compilador");
        setSize(560, 730);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(3);
        setAlwaysOnTop(true);
        getContentPane().setBackground(Color.decode("#1A2226"));

        undoManager = new UndoManager();
        undoManager.setLimit(50);

        txtLineas = new JTextArea();
        txtLineas.setEditable(false);
        txtLineas.setFocusable(false);
        txtLineas.setForeground(Color.WHITE);
        txtLineas.setFont(new Font("Default", 0, 16));
        txtLineas.setBackground(Color.decode("#303939"));
        txtLineas.setText("1\n");

        txtConsola = new JTextArea();
        txtConsola.setFont(new Font("Default", 0, 16));
        txtConsola.addKeyListener(this);
        txtConsola.setTabSize(1);
        txtConsola.getDocument().addUndoableEditListener(this);
        txtConsola.setBackground(Color.decode("#303A40"));
        txtConsola.setForeground(Color.WHITE);

        scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBounds(30, 0, 510, 320);
        scrollConsola.getVerticalScrollBar().addAdjustmentListener(e -> {
            scrollLineas.getVerticalScrollBar().setValue(scrollConsola.getVerticalScrollBar().getValue());
        });

        scrollLineas = new JScrollPane(txtLineas);
        scrollLineas.setBounds(0, 0, 30, 320);
        scrollLineas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        txtMensaje = new JTextArea();
        txtMensaje.setBorder(BorderFactory.createBevelBorder(1));
        txtMensaje.setEditable(false);
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.addKeyListener(this);
        txtMensaje.setBackground(Color.decode("#303A40"));
        txtMensaje.setForeground(Color.white);
        txtMensaje.setFont(new Font("", 0, 16));
        txtMensaje.setFocusable(false);

        scrollMensaje = new JScrollPane(txtMensaje);
        scrollMensaje.setBounds(30, 485, 400, 200);
        scrollMensaje.setEnabled(true);
        scrollMensaje.setBackground(Color.decode("#303A40"));
        scrollMensaje.setForeground(Color.WHITE);

        iniciarTabla();
        scrollTable = new JScrollPane(tablaSimbolos);
        scrollTable.setBounds(30, 330, 490, 150);
        scrollTable.getViewport().setBackground(Color.decode("#303939"));

        btnCompilar = new JButton("Compilar");
        btnCompilar.setBounds(435, 485, 85, 40);
        btnCompilar.addActionListener(this);
        btnCompilar.setBackground(Color.decode("#58FA58"));

        btnAbrirArchivo = new JButton("Abrir");
        btnAbrirArchivo.setBounds(435, 525, 85, 40);
        btnAbrirArchivo.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Abrir archivo para compilar...");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos TXT", "txt");
            fileChooser.setFileFilter(filter);
            int seleccion = fileChooser.showOpenDialog(this);

            if (seleccion == JFileChooser.APPROVE_OPTION)
                leerArchivo(fileChooser.getSelectedFile().getAbsolutePath());
        });
        btnAbrirArchivo.setBackground(new Color(240, 240, 240));

        add(btnCompilar);
        add(btnAbrirArchivo);
        add(scrollLineas);
        add(scrollConsola);
        add(scrollMensaje);
        add(scrollTable);

        setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            txtLineas.append(++lineas + "\n");
        }
        if ((e.getKeyCode() == KeyEvent.VK_Z) && (e.isControlDown())) {
            try {
                undoManager.undo();
            } catch (Exception cue) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        if ((e.getKeyCode() == KeyEvent.VK_Y) && (e.isControlDown())) {
            try {
                undoManager.redo();
            } catch (Exception cue) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        /*if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE){

        }*/

        /*if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            String[] lines = txtConsola.getText().split("\r\n|\r|\n");
            System.out.println(txtConsola.getText());
            for (int i=0;i<lines.length;i++){
                txtLineas.setText("");
                txtLineas.append(++i + "\n");
            }
        }*/
    }

    private void iniciarTabla() {
        modelo = new ModeloTabla();
        tablaSimbolos = new JTable(modelo) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                Color alternateColor = Color.decode("#394D59");
                Color whiteColor = Color.decode("#4D6873");
                if (!comp.getBackground().equals(getSelectionBackground())) {
                    Color c = (row % 2 == 0 ? alternateColor : whiteColor);
                    comp.setBackground(c);
                    comp.setForeground(new Color(230, 230, 230));
                }
                return comp;
            }
        };
        tablaSimbolos.getTableHeader().setReorderingAllowed(false); // Evitar mover columnas
        tablaSimbolos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Evitar multiselección
        defineColumnas();
    }

    private void defineColumnas() {
        modelo.addColumn("No.");
        modelo.addColumn("Modificador");
        modelo.addColumn("Tipo");
        modelo.addColumn("Identificador");
        modelo.addColumn("Valor");
        modelo.addColumn("Renglón");
        modelo.addColumn("No. de Token");

        tablaSimbolos.setRowHeight(25);
        tablaSimbolos.setFont(new Font("Default", 0, 16));
        TableColumnModel columnas = tablaSimbolos.getColumnModel();
        columnas.getColumn(0).setMaxWidth(30);
        columnas.getColumn(1).setMaxWidth(100);
        columnas.getColumn(2).setMaxWidth(70);
        columnas.getColumn(3).setMaxWidth(80);
        columnas.getColumn(4).setMaxWidth(60);
        columnas.getColumn(5).setMaxWidth(60);
        columnas.getColumn(6).setMaxWidth(100);
    }

    private void leerArchivo(String path) {
        try {
            txtConsola.setText("");
            txtLineas.setText("");
            lineas = 0;
            FileReader archivo = new FileReader(path);
            BufferedReader br;
            br = new BufferedReader(archivo);
            String line = "";

            while ((line = br.readLine()) != null) {
                txtConsola.append(line + "\n");
                txtLineas.append(++lineas + "\n");
            }
        } catch (Exception e) {
        }
    }

    private void generarArchivo() {
        ruta = "codigo.txt";
        File archivo = new File(ruta);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(txtConsola.getText());

            bw.close();
        } catch (Exception ex) {

        }
    }


    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        new Main();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (txtConsola.getText().trim().isEmpty()) return;
        generarArchivo();
        compilar(ruta);
    }

    public void compilar(String ruta) {

        Lexico lexico = new Lexico(ruta);

        ArrayList<String> erroresLexicos = lexico.resultado;
        Tabla tabla;
        Sintactico sintactico = null;

        txtMensaje.setText("");
        for (int i = 0; i < erroresLexicos.size(); i++) {
            txtMensaje.append(erroresLexicos.get(i) + " \n");
        }

        if (erroresLexicos.get(0).equals("No hay errores lexicos")) {
            sintactico = new Sintactico(lexico.tokenRC);
            tabla = new Tabla(lexico.tokenRC);
        }

        ArrayList<String> erroresSintacticos = sintactico.resultadoSintactico;
        ArrayList<String> erroresSemanticos = sintactico.resultadoSemantico;

        if (erroresSintacticos.size() == 0)

        if (erroresSintacticos.size() == 0 && erroresSemanticos.size() == 0) {
            txtMensaje.append("No hay errores sintácticos \n");
            txtMensaje.append("No hay errores semánticos \n");
            txtMensaje.setBorder(BorderFactory.createLineBorder(Color.GREEN,2));
            return;
        }
        txtMensaje.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        if (erroresSintacticos.size() == 0)
            txtMensaje.append("No hay errores sintácticos \n");
        else {
            for (int i = 0; i < erroresSintacticos.size(); i++)
                txtMensaje.append(erroresSintacticos.get(i) + " \n");
        }
        if (erroresSemanticos.size() == 0)
            txtMensaje.append("No hay errores semánticos \n");
        else {
            for (int i = 0; i < erroresSemanticos.size(); i++)
                txtMensaje.append(erroresSemanticos.get(i) + " \n");
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
    }
}

class ModeloTabla extends DefaultTableModel {

    public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    public boolean isCellEditable(int row, int co) {
        return false;
    }
}