package ua.edu.lnu.laboratorywork;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class HeatTransferSimulation {

    public static void main(String[] args) {
        // Параметри труби
        double d1 = 0.05;  // діаметр внутрішній (м)
        double d2 = 0.06;  // діаметр зовнішній (м)
        double L = 1.0;    // довжина труби (м)
        double tc1 = 100;  // температура внутрішньої поверхні (градуси Цельсія)
        double tc2 = 20;   // температура зовнішньої поверхні (градуси Цельсія)
        double lambdaVal = 45;  // коефіцієнт теплопровідності (Вт/(м·оС))

        // Розмірність
        double r1 = d1 / 2;
        double r2 = d2 / 2;

        // Графік температурного профілю
        double[] rValues = linspace(r1, r2, 100);
        double[] tValues = solveTemperatureProfile(rValues, tc1, tc2);

        XYChart chart1 = createChart(rValues, tValues, "Числовий розв'язок (Радіус, м)");
        new SwingWrapper<>(chart1).displayChart();

        // Графік за формулою (8)
        double[] tFormula = solveTemperatureFormula(rValues, tc1, tc2, d1, d2);
        XYChart chart2 = createChart(rValues, tFormula, "Формула (8)");
        new SwingWrapper<>(chart2).displayChart();

        // Обчислення теплових потоків
        double q1 = 2 * lambdaVal * (tc1 - tc2) / d1 * Math.log(d2 / d1);
        double q2 = 2 * lambdaVal * (tc1 - tc2) / d2 * Math.log(d2 / d1);
        double ql = 2 * Math.PI * lambdaVal * (tc1 - tc2) / Math.log(d2 / d1);

        System.out.println("Тепловий потік q1: " + q1 + " Вт/м^2");
        System.out.println("Тепловий потік q2: " + q2 + " Вт/м^2");
        System.out.println("Тепловий потік ql: " + ql + " Вт/м");
    }

    private static double[] linspace(double start, double end, int points) {
        double[] array = new double[points];
        double step = (end - start) / (points - 1);

        for (int i = 0; i < points; i++) {
            array[i] = start + i * step;
        }

        return array;
    }

    private static double[] solveTemperatureProfile(double[] rValues, double tc1, double tc2) {
        double[] tValues = new double[rValues.length];

        // Початкові умови
        double t0 = tc1;
        double r0 = rValues[0];
        tValues[0] = t0;

        // Крок чисельного методу
        double h = (rValues[rValues.length - 1] - rValues[0]) / (rValues.length - 1);

        // Чисельне розв'язання диференціального рівняння методом Ейлера
        for (int i = 1; i < rValues.length; i++) {
            double r = rValues[i];
            double t = tValues[i - 1];

            // Змініть на ваше диференціальне рівняння теплопровідності тут
            double dTdr = -t / r;  // Приклад: dT/dr = -t/r, вам потрібно використовувати ваше рівняння

            // Метод Ейлера
            t += h * dTdr;

            tValues[i] = t;
        }

        return tValues;
    }


    private static double[] solveTemperatureFormula(double[] rValues, double tc1, double tc2, double d1, double d2) {
        // Розв'язок за формулою (8)
        double[] tFormula = new double[rValues.length];

        for (int i = 0; i < rValues.length; i++) {
            double r = rValues[i];
            tFormula[i] = tc1 - (tc1 - tc2) * Math.log(d1 / d2) / Math.log(d1 / r);
        }

        return tFormula;
    }

    private static XYChart createChart(double[] xData, double[] yData, String seriesName) {
        XYChart chart = new XYChartBuilder().width(800).height(600).title(seriesName).xAxisTitle("Радіус (м)").yAxisTitle("Температура (градуси Цельсія)").build();

        XYSeries series = chart.addSeries(seriesName, xData, yData);
        series.setMarker(SeriesMarkers.NONE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        chart.getStyler().setXAxisDecimalPattern("0.000"); // Форматування для виведення метрів
        return chart;
    }
}
