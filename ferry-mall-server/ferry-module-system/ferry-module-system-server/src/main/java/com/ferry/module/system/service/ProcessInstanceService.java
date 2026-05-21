package com.ferry.module.system.service;

import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcessInstanceService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    public ProcessInstanceService(RepositoryService repositoryService,
                                  RuntimeService runtimeService,
                                  TaskService taskService,
                                  HistoryService historyService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
    }

    @Transactional(rollbackFor = Exception.class)
    public String deploy(String name, InputStream bpmnStream) {
        Deployment deployment = repositoryService.createDeployment()
            .name(name)
            .addInputStream(name + ".bpmn20.xml", bpmnStream)
            .deploy();
        return deployment.getId();
    }

    public List<ProcessDefinition> listDefinitions() {
        return repositoryService.createProcessDefinitionQuery()
            .latestVersion()
            .list();
    }

    @Transactional(rollbackFor = Exception.class)
    public String start(String processDefinitionKey, Map<String, Object> variables) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
        return instance.getId();
    }

    public List<Map<String, Object>> myTasks(Long userId) {
        List<Task> tasks = taskService.createTaskQuery()
            .taskAssignee(String.valueOf(userId))
            .list();
        return tasks.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", t.getId());
            map.put("taskName", t.getName());
            map.put("processInstanceId", t.getProcessInstanceId());
            map.put("createTime", t.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeTask(String taskId, Map<String, Object> variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new FerryBusinessException(404, "任务不存在");
        }
        if (variables != null) {
            taskService.complete(taskId, variables);
        } else {
            taskService.complete(taskId);
        }
    }

    public List<Map<String, Object>> history(String processInstanceId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(processInstanceId)
            .finished()
            .list();
        return list.stream().map(h -> {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", h.getId());
            map.put("taskName", h.getName());
            map.put("assignee", h.getAssignee());
            map.put("startTime", h.getStartTime());
            map.put("endTime", h.getEndTime());
            return map;
        }).collect(Collectors.toList());
    }
}
