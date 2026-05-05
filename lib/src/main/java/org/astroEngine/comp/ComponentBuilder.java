package org.astroEngine.comp;

public class ComponentBuilder {
    public static interface UpdateListener {
        public void update(Component comp, float delta);
    }

    private Runnable
            dispose = () -> { },
            wHide = () -> { },
            wShow = () -> { };

    private UpdateListener updateListener = ((comp, delta) -> {});

    public ComponentBuilder update(UpdateListener listener) {
        this.updateListener = listener;
        return this;
    }

    public ComponentBuilder dispose(Runnable dispose) {
        this.dispose = dispose;
        return this;
    }

    public ComponentBuilder wHide(Runnable wHide) {
        this.wHide = wHide;
        return this;
    }

    public ComponentBuilder wShow(Runnable wShow) {
        this.wShow = wShow;
        return this;
    }

    public Component build() {
        return new Component(){
            @Override
            public void update(float delta) {
                super.update(delta);
                updateListener.update(this, delta);
            }

            @Override
            public void dispose() {
                super.dispose();
                dispose.run();
            }

            @Override
            public void windowHide() {
                super.windowHide();
                wHide.run();
            }

            @Override
            public void windowShow() {
                super.windowShow();
                wShow.run();
            }
        };
    }
}
