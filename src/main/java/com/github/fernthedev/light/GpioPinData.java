package com.github.fernthedev.light;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class GpioPinData {
    @NonNull
    private GpioPinDigitalOutput output;
    @NonNull
    private Pin pin;

    private int pinInt;
}
