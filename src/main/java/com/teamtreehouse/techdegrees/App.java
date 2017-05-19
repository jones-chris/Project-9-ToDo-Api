package com.teamtreehouse.techdegrees;


import com.google.gson.Gson;
import com.teamtreehouse.techdegrees.dao.Sql2oTodoDao;
import com.teamtreehouse.techdegrees.dao.TodoDao;
import com.teamtreehouse.techdegrees.model.Todo;
import org.h2.store.LimitInputStream;
import org.sql2o.Sql2o;

import java.util.List;

import static spark.Spark.*;

public class App {

    public static void main(String[] args) {
        staticFileLocation("/public");
        String connectionString = "jdbc:h2:~/todos.db;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        //Sql2o sql2o = new Sql2o("jdbc:h2:mem:sample;INIT=RUNSCRIPT from 'classpath:db/init.sql'" );
        TodoDao todoDao = new Sql2oTodoDao(sql2o);
        Gson gson = new Gson();


        get("/api/v1/todos", "application/json",
                (request, response) -> todoDao.findAll(), gson::toJson);

        post("/api/v1/todos", "application/json", ((request, response) -> {
            Todo todo = gson.fromJson(request.body(), Todo.class);
            todoDao.add(todo);
            response.status(201);
            return todoDao.findAll();
        }), gson::toJson);

        put("/api/v1/todos/:id", "application/json", ((request, response) -> {
            Long id = Long.parseLong(request.params("id"));
            Todo todo = todoDao.findById(id);
            todoDao.update(todo);
            return todoDao.findAll();
        }), gson::toJson);

        /*post("/api/v1/todos", "application/json", (request, response) -> {
            Todo todo = gson.fromJson(request.body(), Todo.class);
        });*/

        //get("/blah", (req, res) -> "Hello!");

        after(((request, response) -> {
            response.type("application/json");
        }));
    }

}
