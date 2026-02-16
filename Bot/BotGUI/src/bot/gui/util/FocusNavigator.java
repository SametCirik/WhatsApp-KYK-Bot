package bot.gui.util;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class FocusNavigator {

    /**
     * Verilen bileşen listesinde Yukarı/Aşağı ok tuşlarıyla gezilmeyi sağlar.
     * @param components Sıralı text field listesi
     */
    public static void enableArrowNavigation(List<? extends JComponent> components) {
        for (int i = 0; i < components.size(); i++) {
            JComponent current = components.get(i);
            
            // Önceki ve Sonraki bileşeni belirle (Listenin başı ve sonu için null olabilir)
            JComponent prev = (i > 0) ? components.get(i - 1) : null;
            JComponent next = (i < components.size() - 1) ? components.get(i + 1) : null;

            // --- YUKARI OK TUŞU (UP) ---
            if (prev != null) {
                // "UP" tuşuna basıldığında "goUp" aksiyonunu tetikle
                current.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("UP"), "goUp");
                current.getActionMap().put("goUp", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        prev.requestFocusInWindow(); // Öncekine odaklan
                    }
                });
            }

            // --- AŞAĞI OK TUŞU (DOWN) ---
            if (next != null) {
                // "DOWN" tuşuna basıldığında "goDown" aksiyonunu tetikle
                current.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DOWN"), "goDown");
                current.getActionMap().put("goDown", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        next.requestFocusInWindow(); // Sonrakine odaklan
                    }
                });
            }
            
            // --- ENTER TUŞU (İsteğe bağlı: Enter da aşağı indirsin) ---
            if (next != null) {
                current.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "goNext");
                current.getActionMap().put("goNext", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        next.requestFocusInWindow();
                    }
                });
            }
        }
    }
}