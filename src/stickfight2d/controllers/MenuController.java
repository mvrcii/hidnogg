package stickfight2d.controllers;


import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import kuusisto.tinysound.Music;
import stickfight2d.GameLoop;
import stickfight2d.Main;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.SoundType;
import stickfight2d.misc.Config;
import stickfight2d.misc.Debugger;
import stickfight2d.misc.KeySet;

import java.util.ArrayList;
import java.util.List;

public class MenuController implements Controller {

    private static MenuController instance;
    private static Group root;
    private static Stage primaryStage;
    private final KeyController keyCon = KeyController.getInstance();

    private VBox menuBox;
    private Menu menu;
    private String lastMenu = "main";
    private boolean inGame = false;
    private DirectionType dir = DirectionType.LEFT;

    private Music mainMenuMusic;

    public MenuController() {
        root = Main.getRoot();
        primaryStage = Main.getPrimaryStage();
        menuBox = new VBox();
        menuBox.layoutXProperty().bind(primaryStage.widthProperty().divide(2).subtract(menuBox.widthProperty().divide(2)));
        menuBox.layoutYProperty().bind(primaryStage.heightProperty().divide(2).subtract(menuBox.heightProperty().divide(2)));
        root.getChildren().addAll(menuBox);

        mainMenuMusic = SoundController.getInstance().getMusic(SoundType.MUSIC_MAIN_MENU);
        mainMenuMusic.play(true, Config.volume);


        root.setOnKeyPressed(keyEvent -> {
            processInput(keyEvent);
        });
        displayMenu("main");

    }

    public static MenuController getInstance() {
        if (instance == null) {
            Debugger.log("MenuController instantiated");
            instance = new MenuController();
        }
        return instance;
    }

    public void processInput(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            displayMenu(lastMenu);
        }
        if (!inGame) {
            int oldIndex = menu.getSelectedIndex();
            for (KeySet keySet : new KeySet[]{Config.keySet1, Config.keySet2}) {
                KeyCode keyCode = keyEvent.getCode();
                if (keyCode.equals(keySet.getUpKey())) {
                    menu.getMenuItems().get(oldIndex).exitAnimation();
                    menu.getMenuItems().get(Math.floorMod(oldIndex - 1, menu.getMenuItems().size())).enterAnimation();
                } else if (keyCode.equals(keySet.getDownKey())) {
                    menu.getMenuItems().get(oldIndex).exitAnimation();
                    menu.getMenuItems().get(Math.floorMod(oldIndex + 1, menu.getMenuItems().size())).enterAnimation();
                } else if (keyCode.equals(keySet.getMoveLeftKey())) {
                    if (menu.getTitle().toString().equals("Options")) {
                        dir = DirectionType.LEFT;
                        menu.getMenuItems().get(oldIndex).runOnClick();
                    }
                } else if (keyCode.equals(keySet.getMoveRightKey())) {
                    if (menu.getTitle().toString().equals("Options")) {
                        dir = DirectionType.RIGHT;
                        menu.getMenuItems().get(oldIndex).runOnClick();
                    }
                } else if (keyCode.equals(keySet.getStabKey())) {
                    menu.getMenuItems().get(oldIndex).runOnClick();
                }

            }
        }

    }

    @Override
    public void update(long diffMillis) {
    }

    public void displayMenu(String s) {
        disablePlayerInput(true);
        menuBox.getChildren().clear();
        inGame = false;
        switch (s.toLowerCase()) {
            case "main":
                menu = getMainMenu();
                lastMenu = s;
                break;
            case "pause":
                menu = getPauseMenu();
                lastMenu = s;
                break;
            case "options":
                menu = getOptionsMenu();
                break;
            case "credits":
                menu = getCreditsMenu();
                break;
            case "none":
                inGame = true;
                menu = null;
                disablePlayerInput(false);
                lastMenu = "pause";
                break;
            default:
                Debugger.log("No menu found with this name: " + s);
        }
        if (menu != null) {
            menuBox.getChildren().add(menu);
            menu.getMenuItems().get(menu.getSelectedIndex()).enterAnimation();
        }
    }

    private Menu getMainMenu() {
        Title title = new Title("Main Menu");
        List menuItems = new ArrayList<MenuItem>() {{
            add(new MenuItem("Start Game", () -> {
                displayMenu("none");
                GameLoop.startCounter();
                mainMenuMusic.stop();
                GameLoop.currentMusic.play(false,Config.volume);
            }));
            add(new MenuItem("Options", () -> {
                displayMenu("options");
            }));
            add(new MenuItem("Credits", () -> {
                displayMenu("credits");
            }));
            add(new MenuItem("Exit Game", () -> {
                System.exit(0);
            }));
        }};
        Menu menu = new Menu(title, menuItems);
        return menu;
    }

    private Menu getPauseMenu() {
        Title title = new Title("Pause");
        List menuItems = new ArrayList<MenuItem>() {{
            add(new MenuItem("Resume", () -> {
                displayMenu("none");
            }));
            add(new MenuItem("Options", () -> {
                displayMenu("options");
            }));
            add(new MenuItem("Credits", () -> {
                displayMenu("credits");
            }));
            add(new MenuItem("Title Screen", () -> {
                displayMenu("main");
            }));
        }};
        Menu menu = new Menu(title, menuItems);
        return menu;
    }

    private Menu getOptionsMenu() {
        Title title = new Title("Options");
        List menuItems = new ArrayList<MenuItem>() {{
            add(new MenuItem("\uD83E\uDC44Volume:" + Math.round(Config.volume * 1000) + "%\uD83E\uDC46", () -> {
                double newVolume = Config.volume + (dir.equals(DirectionType.LEFT) ? -0.005 : 0.005);
                Config.volume = Math.min(Math.max(newVolume, 0), 0.1);
                GameLoop.currentMusic.setVolume(Config.volume);
                mainMenuMusic.setVolume(Config.volume);
                menu.getMenuItems().get(menu.getSelectedIndex()).setText("\uD83E\uDC44Volume:" + Math.round(Config.volume * 1000) + "%\uD83E\uDC46");
            }));
            add(new MenuItem("\uD83E\uDC44Debug Mode:" + booleanToOnOff(Config.debug_mode) + "\uD83E\uDC46", () -> {
                Config.debug_mode = !Config.debug_mode;
                menu.getMenuItems().get(menu.getSelectedIndex()).setText("\uD83E\uDC44Debug Mode:" + booleanToOnOff(Config.debug_mode) + "\uD83E\uDC46");
            }));
            add(new MenuItem("\uD83E\uDC44FPS Counter:" + booleanToOnOff(Config.fps_print_mode) + "\uD83E\uDC46", () -> {
                Config.fps_print_mode = !Config.fps_print_mode;
                GameLoop.currentLevel.getFpsObject().setPrintMode(Config.fps_print_mode);
                menu.getMenuItems().get(menu.getSelectedIndex()).setText("\uD83E\uDC44FPS Counter:" + booleanToOnOff(Config.fps_print_mode) + "\uD83E\uDC46");
            }));
            add(new MenuItem("Back", () -> {
                displayMenu(lastMenu);
            }));
        }};
        Menu menu = new Menu(title, menuItems);
        return menu;
    }

    private String booleanToOnOff(boolean b) {
        return b ? "On" : "Off";
    }

    private Menu getCreditsMenu() {
        Title title = new Title("Credits");
        List menuItems = new ArrayList<MenuItem>() {{
            add(new Credits());
            add(new MenuItem("Back", () -> {
                displayMenu(lastMenu);
            }));
        }};
        Menu menu = new Menu(title, menuItems);
        menu.setSelectedIndex(1);
        return menu;
    }

    private void disablePlayerInput(boolean disable) {
        KeyController.getInstance().setKeyPressBlockedP1(disable);
        KeyController.getInstance().setKeyPressBlockedP2(disable);
        //GameLoop.currentLevel.getPlayer1().setInputDisabled(disable);
        //GameLoop.currentLevel.getPlayer2().setInputDisabled(disable);
    }

    private class Credits extends MenuItem {
        public Credits() {
            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setText(Config.CREDITS);
            textArea.setPrefHeight(470);
            textArea.setPrefWidth(400);
            textArea.setStyle("-fx-control-inner-background:#D3D3D3;");
            getChildren().add(textArea);
        }

        public void runOnClick() {
        }

        public void enterAnimation() {
            menu.setSelectedIndex(menu.getMenuItems().indexOf(this));
        }

        public void exitAnimation() {
        }
    }

    private class Menu extends VBox {
        private Title title;
        private List<MenuItem> menuItems;
        private int selectedIndex;

        public Menu(Title title, List<MenuItem> menuItems) {
            this.title = title;
            this.menuItems = menuItems;
            getChildren().add(title);
            getChildren().addAll(menuItems);
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

        public void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

        public List<MenuItem> getMenuItems() {
            return menuItems;
        }

        public Title getTitle() {
            return title;
        }
    }

    private class MenuItem extends StackPane {
        private ScaleTransition st;
        private FadeTransition ft;
        private Runnable onClick;
        private Text textField;

        public MenuItem(String text, Runnable onClick) {
            this.onClick = onClick;
            setOnMouseClicked(event -> {
                dir = event.getX()>200 ? DirectionType.RIGHT : DirectionType.LEFT;
                this.onClick.run();
            });
            textField = new Text(text);
            textField.setFill(Config.FONT_COLOR);
            textField.setFont(Font.loadFont(Config.FONT_PATH, 50));

            Rectangle bg = new Rectangle(400, textField.getLayoutBounds().getHeight() + 3);
            LinearGradient lg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.TRANSPARENT), new Stop(0.2, Color.GRAY), new Stop(0.8, Color.GRAY), new Stop(1, Color.TRANSPARENT));
            bg.setFill(lg);
            st = new ScaleTransition(Duration.millis(200), this);
            ft = new FadeTransition(Duration.millis(200), this);
            this.setOpacity(0.3);
            setOnMouseEntered(event -> {
                menu.getMenuItems().get(menu.getSelectedIndex()).exitAnimation();
                enterAnimation();
            });
            setOnMouseExited(event -> {
                exitAnimation();
            });

            getChildren().addAll(bg, textField);
        }

        private void startAnimations() {
            st.play();
            ft.play();
        }

        private void stopAnimations() {
            st.stop();
            ft.stop();
        }

        private void setAnimationsSettings(boolean reverse) {
            if (reverse) {
                ft.setFromValue(1.0);
                ft.setToValue(0.3);
                st.setToX(1);
                st.setToY(1);
            } else {
                ft.setFromValue(0.3);
                ft.setToValue(1.0);
                st.setToX(1.3f);
                st.setToY(1.3f);
            }
        }

        public MenuItem() {
        }
        public void setText(String s){
            textField.setText(s);
        }
        public void runOnClick() {
            onClick.run();
        }

        public void enterAnimation() {
            menu.setSelectedIndex(menu.getMenuItems().indexOf(this));
            stopAnimations();
            setAnimationsSettings(false);
            startAnimations();
        }

        public void exitAnimation() {
            stopAnimations();
            setAnimationsSettings(true);
            startAnimations();
        }
    }

    private class Title extends StackPane {
        String text;
        public Title(String text) {
            this.text = text;
            Text t = new Text(text);
            t.setFill(Config.FONT_MENU);
            t.setFont(Font.loadFont(Config.FONT_PATH, 60));

            Rectangle bg = new Rectangle(400, t.getLayoutBounds().getHeight() + 3);
            bg.setStrokeWidth(4);
            bg.setFill(Color.GRAY);

            getChildren().addAll(bg, t);
        }
        @Override
        public String toString() {
            return text;
        }
    }
}
