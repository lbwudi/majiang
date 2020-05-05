package liu.client.myEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseAdapter_Gang extends MouseAdapter {
    private boolean Gang;

    @Override
    public void mouseClicked(MouseEvent e) {
        setGang(true);
    }

    public boolean getGang() {
        if(Gang){
            boolean b = Gang;
            Gang = false;
            return b;
        }
        return false;
    }

    public void setGang(boolean gang) {
        Gang = gang;
    }
}
