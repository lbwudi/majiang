package liu.client.myEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseAdapter_Hu extends MouseAdapter {
    private boolean Hu;

    @Override
    public void mouseClicked(MouseEvent e) {
        setHu(true);
    }

    public boolean getHu() {
        if(Hu){
            boolean b = Hu;
            Hu = false;
            return b;
        }
        return false;
    }

    public void setHu(boolean hu) {
        Hu = hu;
    }
}
