package app;

import controls.Label;
import io.github.humbleui.jwm.*;
import io.github.humbleui.jwm.skija.EventFrameSkija;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.RRect;
import io.github.humbleui.skija.Surface;
import misc.CoordinateSystem2i;
import misc.Misc;

import java.io.File;
import java.util.function.Consumer;

import static app.Colors.APP_BACKGROUND_COLOR;
import static app.Colors.PANEL_BACKGROUND_COLOR;

/**
 * Класс окна приложения
 */
public class Application implements Consumer<Event> {
    /**
     * окно приложения
     */
    private final Window window;
    /**
     * радиус скругления элементов
     */
    public static final int C_RAD_IN_PX = 4;
    /**
     * отступы панелей
     */
    public static final int PANEL_PADDING = 5;
    /**
     * Первый заголовок
     */
    private final Label label;
    /**
     * Первый заголовок
     */
    private final Label label2;
    /**
     * Первый заголовок
     */
    private final Label label3;
    /**
     * Конструктор окна приложения
     */
    // конструктор приложения
    public Application() {
        // создаём окно
        window = App.makeWindow();

        // создаём первый заголовок
        label = new Label(window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING,
                4, 4, 1, 1, 1, 1, "Привет, мир!", true, true);
        // создаём второй заголовок
        label2 = new Label(window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING,
                4, 4, 0, 3, 1, 1, "Второй заголовок", true, true);

        // создаём третий заголовок
        label3 = new Label(window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING,
                4, 4, 2, 0, 1, 1, "Это тоже заголовок", true, true);

        window.setEventListener(this);
        window.setTitle("Java 2D");
        window.setWindowSize(900, 900);
        window.setWindowPosition(100, 100);
        switch (Platform.CURRENT) {
            case WINDOWS -> window.setIcon(new File("src/main/resources/windows.ico"));
            case MACOS -> window.setIcon(new File("src/main/resources/macos.icns"));
        }
        String[] layerNames = new String[]{
                "LayerGLSkija", "LayerRasterSkija"
        };

        for (String layerName : layerNames) {
            String className = "io.github.humbleui.jwm.skija." + layerName;
            try {
                Layer layer = (Layer) Class.forName(className).getDeclaredConstructor().newInstance();
                window.setLayer(layer);
                break;
            } catch (Exception e) {
                System.out.println("Ошибка создания слоя " + className);
            }
        }
        if (window._layer == null)
            throw new RuntimeException("Нет доступных слоёв для создания");
        window.setVisible(true);
    }

    /**
     * Обработчик событий
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        if (e instanceof EventWindowClose) {
            // завершаем работу приложения
            App.terminate();
        }else if (e instanceof EventWindowCloseRequest) {
            window.close();
        }
        else if (e instanceof EventFrameSkija ee) {
            Surface s = ee.getSurface();
            paint(s.getCanvas(), new CoordinateSystem2i(
                    s.getWidth() / 3, s.getHeight() / 3,
                    s.getWidth() / 3,  s.getHeight() / 3));
        }
    }
    /**
     * Рисование
     *
     * @param canvas   низкоуровневый инструмент рисования примитивов от Skija
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // запоминаем изменения (пока что там просто заливка цветом)
        canvas.save();
        // очищаем канвас
        canvas.clear(APP_BACKGROUND_COLOR);
        label.paint(canvas, windowCS);
        // рисуем второй заголовок
        label2.paint(canvas, windowCS);
        // рисуем третий заголовок
        label3.paint(canvas, windowCS);
        // восстанавливаем состояние канваса
        canvas.restore();
    }
}