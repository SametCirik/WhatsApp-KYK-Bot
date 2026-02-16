package bot.gui.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class PlaceholderText extends JTextField 
{
    private String placeholder; // public'ten private'a çevirmek daha iyidir.
    private boolean hasFocus;

    public PlaceholderText(String placeholder) {
        this.placeholder = placeholder;
        this.hasFocus = false; 
        setMargin(new Insets(0, 6, 0, 0));

        super.addFocusListener(new FocusListener() 
        {
            @Override
            public void focusGained(FocusEvent e) { hasFocus = true; repaint(); }
            @Override
            public void focusLost(FocusEvent e) { hasFocus = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !hasFocus) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.drawString(placeholder, 5, getHeight() / 2 + getFont().getSize() / 2 - 2);
            g2.dispose();
        }
    }
    
    // --- İstenen Metot ---
    /**
     * Yer tutucu metni (placeholder) döndürür.
     * @return Yer tutucu olarak ayarlanan String.
     */
    public String getPlaceholder() {
        return placeholder;
    }
    // -----------------------

    public void setPlaceholder(String newPlaceholder) {
        this.placeholder = newPlaceholder;
        repaint();
    }
}