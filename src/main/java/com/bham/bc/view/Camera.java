package com.bham.bc.view;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;

public class Camera {
    private DoubleProperty x;
    private DoubleProperty y;

    private ObservableList<DoubleProperty> xs;
    private ObservableList<DoubleProperty> ys;

    public static Camera camera = new Camera();

    public Camera() {
        xs = FXCollections.observableArrayList(x -> new Observable[] {x});
        ys = FXCollections.observableArrayList(y -> new Observable[] {y});
        x = new SimpleDoubleProperty();
        y = new SimpleDoubleProperty();
    }

    public void addTrackableCoordinate(DoubleProperty x, DoubleProperty y) {
        xs.add(x);
        ys.add(y);

        this.x.bind(Bindings.add(
                Bindings.divide(Bindings.createDoubleBinding(() -> xs.stream().mapToDouble(DoubleProperty::get).reduce(Double::min).getAsDouble()), 2),
                Bindings.divide(Bindings.createDoubleBinding(() -> xs.stream().mapToDouble(DoubleProperty::get).reduce(Double::max).getAsDouble()), 2)));
        this.y.bind(Bindings.add(
                Bindings.divide(Bindings.createDoubleBinding(() -> ys.stream().mapToDouble(DoubleProperty::get).reduce(Double::min).getAsDouble()), 2),
                Bindings.divide(Bindings.createDoubleBinding(() -> ys.stream().mapToDouble(DoubleProperty::get).reduce(Double::max).getAsDouble()), 2)));
    }

    public void update(GraphicsContext gc) {

    }
}
