package com.example.nosmogrunner;


import java.util.ArrayList;

public interface TaskLoadedCallback {
    void onTaskDone(ArrayList... values);
}