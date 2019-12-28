package com.github.fernthedev.light.animations;

import com.github.fernthedev.light.LightManager;
import com.github.fernthedev.light.ReflectionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.lang.reflect.*;
import java.util.*;

/**
 * Animations ported from python to Java
 * @link https://tutorials-raspberrypi.com/how-to-control-a-raspberry-pi-ws2801-rgb-led-strip/
 *
 * # Simple demo of of the WS2801/SPI-like addressable RGB LED lights.
 * import time
 * import RPi.GPIO as GPIO
 *
 * # Import the WS2801 module.
 * import Adafruit_WS2801
 * import Adafruit_GPIO.SPI as SPI
 *
 *
 * # Configure the count of pixels:
 * PIXEL_COUNT = 32
 *
 * # Alternatively specify a hardware SPI connection on /dev/spidev0.0:
 * SPI_PORT   = 0
 * SPI_DEVICE = 0
 * pixels = Adafruit_WS2801.WS2801Pixels(PIXEL_COUNT, spi=SPI.SpiDev(SPI_PORT, SPI_DEVICE), gpio=GPIO)
 *
 *
 * # Define the wheel function to interpolate between different hues.
 * def wheel(pos):
 *     if pos < 85:
 *         return Adafruit_WS2801.RGB_to_color(pos * 3, 255 - pos * 3, 0)
 *     elif pos < 170:
 *         pos -= 85
 *         return Adafruit_WS2801.RGB_to_color(255 - pos * 3, 0, pos * 3)
 *     else:
 *         pos -= 170
 *         return Adafruit_WS2801.RGB_to_color(0, pos * 3, 255 - pos * 3)
 *
 * # Define rainbow cycle function to do a cycle of all hues.
 * def rainbow_cycle_successive(pixels, wait=0.1):
 *     for i in range(pixels.count()):
 *         # tricky math! we use each pixel as a fraction of the full 96-color wheel
 *         # (thats the i / strip.numPixels() part)
 *         # Then add in j which makes the colors go around per pixel
 *         # the % 96 is to make the wheel cycle around
 *         pixels.set_pixel(i, wheel(((i * 256 // pixels.count())) % 256) )
 *         pixels.show()
 *         if wait > 0:
 *             time.sleep(wait)
 *
 * def rainbow_cycle(pixels, wait=0.005):
 *     for j in range(256): # one cycle of all 256 colors in the wheel
 *         for i in range(pixels.count()):
 *             pixels.set_pixel(i, wheel(((i * 256 // pixels.count()) + j) % 256) )
 *         pixels.show()
 *         if wait > 0:
 *             time.sleep(wait)
 *
 * def rainbow_colors(pixels, wait=0.05):
 *     for j in range(256): # one cycle of all 256 colors in the wheel
 *         for i in range(pixels.count()):
 *             pixels.set_pixel(i, wheel(((256 // pixels.count() + j)) % 256) )
 *         pixels.show()
 *         if wait > 0:
 *             time.sleep(wait)
 *
 * def brightness_decrease(pixels, wait=0.01, step=1):
 *     for j in range(int(256 // step)):
 *         for i in range(pixels.count()):
 *             r, g, b = pixels.get_pixel_rgb(i)
 *             r = int(max(0, r - step))
 *             g = int(max(0, g - step))
 *             b = int(max(0, b - step))
 *             pixels.set_pixel(i, Adafruit_WS2801.RGB_to_color( r, g, b ))
 *         pixels.show()
 *         if wait > 0:
 *             time.sleep(wait)
 *
 * def blink_color(pixels, blink_times=5, wait=0.5, color=(255,0,0)):
 *     for i in range(blink_times):
 *         # blink two times, then wait
 *         pixels.clear()
 *         for j in range(2):
 *             for k in range(pixels.count()):
 *                 pixels.set_pixel(k, Adafruit_WS2801.RGB_to_color( color[0], color[1], color[2] ))
 *             pixels.show()
 *             time.sleep(0.08)
 *             pixels.clear()
 *             pixels.show()
 *             time.sleep(0.08)
 *         time.sleep(wait)
 *
 * def appear_from_back(pixels, color=(255, 0, 0)):
 *     pos = 0
 *     for i in range(pixels.count()):
 *         for j in reversed(range(i, pixels.count())):
 *             pixels.clear()
 *             # first set all pixels at the begin
 *             for k in range(i):
 *                 pixels.set_pixel(k, Adafruit_WS2801.RGB_to_color( color[0], color[1], color[2] ))
 *             # set then the pixel at position j
 *             pixels.set_pixel(j, Adafruit_WS2801.RGB_to_color( color[0], color[1], color[2] ))
 *             pixels.show()
 *             time.sleep(0.02)
 *
 *
 * if __name__ == "__main__":
 *     # Clear all the pixels to turn them off.
 *     pixels.clear()
 *     pixels.show()  # Make sure to call show() after changing any pixels!
 *
 *     rainbow_cycle_successive(pixels, wait=0.1)
 *     rainbow_cycle(pixels, wait=0.01)
 *
 *     brightness_decrease(pixels)
 *
 *     appear_from_back(pixels)
 *
 *     for i in range(3):
 *         blink_color(pixels, blink_times = 1, color=(255, 0, 0))
 *         blink_color(pixels, blink_times = 1, color=(0, 255, 0))
 *         blink_color(pixels, blink_times = 1, color=(0, 0, 255))
 *
 *
 *
 *     rainbow_colors(pixels)
 *
 *     brightness_decrease(pixels)
 *
 */
public class Animations implements IAnimation {

    private static final List<Class<? extends IAnimation>> animationClasses;


    private static List<Class<?>> supportedClassTypes = new ArrayList<>(Arrays.asList(ReflectionUtil.validClassTypes));

    private static final Map<String, InstanceMemberPair<?, ? extends Member>> animationToMemberMap = new HashMap<>();

    public static @NonNull AnimationRunnable runAnimation(String name, LedStrip ledStrip, String... args) {
        InstanceMemberPair<?, ? extends Member> instanceMemberPair = animationToMemberMap.get(name);
        Object instance = instanceMemberPair.getInstance();
        Member member = instanceMemberPair.getMemberReflection();

        if (member instanceof Method) {
            Method method = (Method) member;

            try {
                if(ledStrip == null) throw new IllegalArgumentException("LedStrip is used in a method, cannot be null");

                List<Object> fulfilledPar = new ArrayList<>();

                // No longer required
//                fulfilledPar.add(ledStrip);
//
//                Parameter[] parList = new Parameter[method.getParameters().length - 1];
//
//
//                System.arraycopy(method.getParameters(), 1, parList, 0, method.getParameters().length - 1);

                Parameter[] parList = method.getParameters();


                Queue<String> argQueue = new LinkedList<>(Arrays.asList(args));
                List<Parameter> missingPar = null;

                for (Parameter parameter : parList) {

                    if (argQueue.isEmpty()) {
                        if(missingPar == null) missingPar = new ArrayList<>();
                        missingPar.add(parameter);
                        continue;
                    }

                    Object o = ReflectionUtil.parseObject(parameter.getType(), null, argQueue.remove());

                    fulfilledPar.add(o);

                    LightManager.getLogger().debug("Added {} of type {}", o, o.getClass().getName());
                }

                if (missingPar != null) {

                    Map<String, Class<?>> parMap = new HashMap<>();

                    for (Parameter parameter : missingPar) {
                        parMap.put(parameter.getName(), parameter.getType());
                    }

                    throw new IllegalArgumentException("Not enough arguments provided. Missing arguments for parameters: " + parMap.toString());
                }


                return (AnimationRunnable) method.invoke(instance, fulfilledPar.toArray(new Object[0]));
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                throw new IllegalArgumentException("Unable to invoke " + method.getName(), e);
            }

        } else if (member instanceof Field) {
            Field field = (Field) member;

            try {
                return (AnimationRunnable) field.get(instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("The member type is not field or method. Check: " + member + " for class " + instance.getClass());
    }

    private static final Animations instance;

    static {
        animationClasses = new ArrayList<>();
        supportedClassTypes.add(LedStrip.class);
        instance = new Animations();
        registerAnimation(instance);
    }

    public static void registerAnimation(IAnimation iAnimation) {

        Class<? extends IAnimation> animationClass = iAnimation.getClass();

        Map<String, InstanceMemberPair<IAnimation, ? extends Member>> memberPairMap = new HashMap<>();

        for (Field field : animationClass.getDeclaredFields()) {

            if (field.isAnnotationPresent(AnimationName.class)) {
                if (!field.getType().isAssignableFrom(AnimationRunnable.class)) {
                    throw new IllegalArgumentException("The field type must be AnimationRunnable");
                }

                AnimationName animationName = field.getAnnotation(AnimationName.class);

                memberPairMap.put(animationName.name(), new InstanceMemberPair<>(iAnimation, field));
            }
        }

        for (Method method : animationClass.getDeclaredMethods()) {
            if(method.isAnnotationPresent(AnimationName.class)) {

                if (!method.getReturnType().isAssignableFrom(AnimationRunnable.class)) {
                    throw new IllegalArgumentException("The return type must be AnimationRunnable");
                }
//                  No longer needed. It is provided when running the runnable
//                if (method.getParameters().length == 0 || !method.getParameters()[0].getType().isAssignableFrom(LedStrip.class)) {
//                    throw new IllegalArgumentException("All methods must have a LedStrip instance as the first parameter. Method: " + method.getName() + ":" + method +
//                            "  parameter");
//                }
//
//                int length = method.getParameterTypes().length - 1;
//
//                Class<?>[] allPars = new Class[length];
//
//                System.arraycopy(method.getParameterTypes(), 1, allPars, 0, length);

                Class<?>[] allPars = method.getParameterTypes();

                for (Class<?> par : allPars) {

                    if (!ReflectionUtil.isValid(supportedClassTypes, par)) {
                        throw new IllegalArgumentException("The method " + method.getName() + ":" + method +
                                "  parameter " + par.getName() + " with type " + par.getTypeName() + " is not supported.");
                    }
                }

                AnimationName animationName = method.getAnnotation(AnimationName.class);

                memberPairMap.put(animationName.name(), new InstanceMemberPair<>(iAnimation, method));

            }
        }

        animationClasses.add(animationClass);

        for (String s : memberPairMap.keySet()) {
            if (Animations.animationToMemberMap.containsKey(s)) {
                InstanceMemberPair<?, ? extends Member> instancePair = Animations.animationToMemberMap.get(s);

                throw new IllegalArgumentException("The name for animation " + s + " is taken by class " + instancePair.instance + " in member " + instancePair.memberReflection);
            }

            Animations.animationToMemberMap.put(s, memberPairMap.get(s));
        }
    }

    @Setter
    @Getter
    private int colorSpace = 256;

    public static RGBColor wheel(int pos) {
        if (pos < 85) {
            return new RGBColor(pos * 3, 255 - pos * 3, 0);
        } else if (pos < 170) {
            return new RGBColor(255 - pos * 3, 0, pos * 3);
        } else {
            return new RGBColor(0, pos * 3, 255 - pos * 3);
        }
    }

    /**
     * Cycles through all the colors for each LED has it's own color and it follows a circle pattern repeating until the cycle begins again
     * @param waitMS
     * @return
     */
    @AnimationName(name = "rainbow_cycle", documentation = "Cycles through all the colors for each LED has it's own color and it follows a circle pattern repeating until the cycle begins again")
    public AnimationRunnable rainbowCycle(long waitMS) {
        return strip -> {
            for (int color = 0; color < getColorSpace(); color++) {
                for (int pixel = 0; pixel < strip.getNumberOfLeds(); pixel++) {
                    strip.setLed(pixel, wheel(
                            (
                                    (
                                            color * getColorSpace()
                                    ) / strip.getNumberOfLeds()
                            ) % getColorSpace())
                    );
                }
                strip.update();

                if (waitMS > 0) {
                    try {
                        Thread.sleep(waitMS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        };
    }

    /**
     *
     * @param waitMS
     * @param step
     * @return
     */
    @AnimationName(name = "fade_off", documentation = "Fades all the colors to off. Step is the subtraction caused every wait in milliseconds")
    public AnimationRunnable fadeOff(long waitMS, int step) {
        if (step == 0) step = 1;

        int finalStep = step;
        return strip -> {


            for (int color = 0; color < getColorSpace() / finalStep; color++) {
                for (int pixel = 0; pixel < strip.getNumberOfLeds(); pixel++) {
                    strip.setBrightness(strip.getBrightness() - finalStep);
                }
                strip.update();

                if (waitMS > 0) {
                    try {
                        Thread.sleep(waitMS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        };
    }

    /**
     * Blinks the LEDs
     * @param blinkTimes How many times it blinks
     * @param blinkWaitTimeMS How much time to waitMS to blink
     * @return
     */
    @AnimationName(name = "blink", documentation = "Makes the LEDs blink blink times, blinkWaitTimeMS to wait when to turn on and waitMS for the next blink cycle")
    public AnimationRunnable blink(int blinkTimes, int blinkWaitTimeMS) {
        if (blinkTimes == 0) blinkTimes = 5;

        int finalBlinkTimes = blinkTimes;
        return strip -> {
            for (int i = 0; i < finalBlinkTimes; i++) {
                // Turn off
                LedStrip.RGBLed[] leds = strip.allOff();

                // Wait
                try {
                    Thread.sleep(blinkWaitTimeMS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Set leds to old state
                strip.setLedBuffer(leds);
                strip.update();
            }
        };
    }


    /**
     * Cycles between each LED once with each one with it's own color
     * @param waitMS
     * @return
     */
    @AnimationName(name = "rainbow_cycle_successive", documentation = "Cycles between each LED once with each one with it's own color, waiting time for the next refresh of colors")
    public AnimationRunnable rainbowCycleSuccessive(long waitMS) {
        return strip -> {
                for (int pixel = 0; pixel < strip.getNumberOfLeds(); pixel++) {
                    strip.setLed(pixel, wheel(
                                    (
                                            (
                                                    pixel * getColorSpace()
                                            ) / strip.getNumberOfLeds()
                                    ) % getColorSpace())
                    );
                    strip.update();
                    if (waitMS > 0) {
                        try {
                            Thread.sleep(waitMS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        };
    }

    /**
     * Cycles through all the colors with all the LEDs synced
     * @param waitMS
     * @return
     */
    @AnimationName(name = "rainbow_color", documentation = "Cycles through all the colors with all the LEDs synced, waiting time for the next refresh of colors")
    public AnimationRunnable rainbowColor(long waitMS) {

        return strip1 -> {
            for (int color = 0; color < getColorSpace(); color++) {
                for (int pixel = 0; pixel < strip1.getNumberOfLeds(); pixel++) {
                    strip1.setLed(pixel, wheel(
                                    (

                                                    getColorSpace()
                                             / strip1.getNumberOfLeds() + color
                                    ) % getColorSpace())
                    );
                }
                strip1.update();

                if (waitMS > 0) {
                    try {
                        Thread.sleep(waitMS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
    }

    /**
     * Fills the end of the LED to the start
     * @return
     */
    @AnimationName(name = "fill_from_back", documentation = "Fills the end of the LED to the start")
    public AnimationRunnable fillFromBack(long waitMS) {

        if (waitMS < 0) waitMS = 2;

        long finalWaitMS = waitMS;
        return strip1 -> {
            for (int pixel = 0; pixel < strip1.getNumberOfLeds(); pixel++) {
                float brightness = strip1.getBrightness();
                strip1.setBrightness(0);
                for (int reversePixel = strip1.getNumberOfLeds() - 1; reversePixel >= pixel; reversePixel--) {
//                    System.out.println(reversePixel + " num " + strip1.getNumberOfLeds() + " max " + pixel + "pix");
                    for (int k = 0; k < pixel; k++) {
                        strip1.setBrightness(k, brightness);
                    }

                    strip1.setBrightness(reversePixel, brightness);
                    strip1.update();

                    try {
                        Thread.sleep(finalWaitMS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
    }

    /**
     * Fills the end of the LED to the start
     * @return
     */
    @AnimationName(name = "fill_color", documentation = "Fills the end of the LED to the start")
    public AnimationRunnable fillColor(int red, int green, int blue) {
        return strip -> {
            strip.fill(red, green, blue);
            strip.update();
        };
    }

    @AllArgsConstructor
    @Getter
    private static class InstanceMemberPair<I, M extends Member> {
        private I instance;
        private M memberReflection;
    }
}
