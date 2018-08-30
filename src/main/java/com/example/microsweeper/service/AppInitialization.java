package com.example.microsweeper.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppInitialization implements ServletContextListener
{

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        //Nothing to do
    }
}
