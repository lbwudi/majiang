package liu.client.myEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseAdapter_Peng extends MouseAdapter {
    private boolean Peng;

    @Override
    public void mouseClicked(MouseEvent e) {
        setPeng(true);
    }

    public boolean getPeng() {
        if(Peng){
            boolean b = Peng;
            Peng = false;
            return b;
        }
        return false;
    }

    public void setPeng(boolean peng) {
        Peng = peng;
    }
}
