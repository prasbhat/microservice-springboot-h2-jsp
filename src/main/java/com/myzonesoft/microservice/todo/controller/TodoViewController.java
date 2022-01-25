package com.myzonesoft.microservice.todo.controller;

import com.myzonesoft.microservice.todo.model.Todo;
import com.myzonesoft.microservice.todo.model.TodoTaskComments;
import com.myzonesoft.microservice.todo.util.TodoApplicationConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.text.MessageFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TodoViewController is the Controller class for the To-do Tracker Application.
 * This class is responsible for communication between the JSP pages and the other Controller.
 *
 */
@RestController
@SuppressWarnings("unused")
public class TodoViewController implements TodoApplicationConstants {

    //Variable declarations
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoViewController.class);
    private final String className = this.getClass().getSimpleName();

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${base_uri}")
    private String base_uri;

    /**
     * Method for redirecting to the index.jsp page
     * This method accepts HTTP_REQUEST_METHOD:GET
     * @param model required for ModelAndView
     *
     * @return Redirection to index.jsp using ModelAndView
     */
    @GetMapping("/")
    public ModelAndView gotoHome(ModelMap model) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        LOGGER.info(MessageFormat.format(LOGGER_ENTRY, className, methodName));
        model.put("todoList", restTemplate.getForObject(base_uri,List.class));
        LOGGER.info(MessageFormat.format(LOGGER_EXIT, className, methodName));
        return new ModelAndView("index");
    }

    /**
     * Method for redirecting to the singleItemPage.jsp page
     * This method accepts HTTP_REQUEST_METHOD:GET
     * @param action can be view/edit
     * @param todoId Id of the To-do Task selected
     * @param model required for ModelAndView
     *
     * @return Redirection to singleViewPage.jsp using ModelAndView
     */
    @GetMapping("/singleItemView/{action}/{todoId}")
    public ModelAndView goToSingleItemView(ModelMap model, @PathVariable("action") String action,
                                           @PathVariable("todoId") String todoId){
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        LOGGER.info(MessageFormat.format(LOGGER_ENTRY, className, methodName));
        Todo todoItem = new Todo(0L, "", "", LocalDate.now(), LocalDate.now(), "",
                null);
        if(Long.parseLong(todoId) != 0) {
            todoItem = restTemplate.getForObject(base_uri + "/find/" + todoId, Todo.class);
        }
        model.put("todoItem", todoItem);
        model.put("action",action);
        model.put("todoStatus",restTemplate.getForObject(base_uri+"/getStatus",List.class));
        LOGGER.info(MessageFormat.format(LOGGER_EXIT, className, methodName));
        return new ModelAndView("singleItemPage");
    }

    @PostMapping("/create")
    public void sendToCreate(@RequestBody Todo todoItem) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        LOGGER.info(MessageFormat.format(LOGGER_ENTRY, className, methodName));
        LOGGER.debug("TodoObject="+todoItem);
        restTemplate.postForObject(base_uri+"/create",todoItem,Todo.class);
        LOGGER.info(MessageFormat.format(LOGGER_EXIT, className, methodName));
    }

    @PutMapping("/update")
    public void sendToUpdate(@RequestBody Todo todoItem) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        LOGGER.info(MessageFormat.format(LOGGER_ENTRY, className, methodName));
        LOGGER.debug("TodoObject="+todoItem);
        restTemplate.put(base_uri+"/update",todoItem,Todo.class);
        LOGGER.info(MessageFormat.format(LOGGER_EXIT, className, methodName));
    }

    @DeleteMapping("/deleteById/{id}")
    public void sendToDeleteById(@PathVariable long id) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        LOGGER.info(MessageFormat.format(LOGGER_ENTRY, className, methodName));
        restTemplate.delete(base_uri+"/deleteById/"+id);
        LOGGER.info(MessageFormat.format(LOGGER_EXIT, className, methodName));
    }

}