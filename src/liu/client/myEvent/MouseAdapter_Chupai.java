package liu.client.myEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseAdapter_Chupai extends MouseAdapter {
    private boolean Chupai;

    @Override
    public void mouseClicked(MouseEvent e) {
        setChupai(true);
    }

    public boolean getChupai() {
        if(Chupai){
            boolean b = Chupai;
            Chupai = false;
            return b;
        }
        return false;
    }

    public void setChupai(boolean chupai) {
        Chupai = chupai;
    }
}
