package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;
	
	private List<String> operations = List.of(
			"1) Add a Project",
			"2) List Projects",
			"3) Select a Project",
			"4) Update project details",
			"5) Delete a project"
			);
	
	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}
	private void processUserSelections() {
		boolean done = false;
		
		while(!done) {
			try {
				int selection = getUserSelection();
				
				switch (selection) {
				case -1:
					done = exitMenu();
					break;
					
				case 1:
					createProject();
					break;
					
				case 2:
					listProjects();
					break;
					
				case 3:
					selectProject();
					break;
					
				case 4:
					updateProjectDetails();
					break;
					
				case 5:
					deleteProject();
					break;
					
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					
				}
				}
			catch (Exception e) {
				System.out.println("\nError: " + e + "Try again.");
			}
		}
	}
	
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actaul hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}
	
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
	
		if(Objects.isNull(curProject)) {
			System.out.println("\nThat is not a vaild project'");
		}
	}
	
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects");
		
		projects.forEach(project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));
	}
	
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return new BigDecimal(input).setScale(2);
			
		}
		catch(NumberFormatException e) {
			throw new DbException(input + "is not a valid decimal number.");
			
		}
	}
	
	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	}
	
	private int getUserSelection() {
		printOperations();
		
		Integer input = getIntInput("Enter a menu selection");
				
		return Objects.isNull(input) ? -1 :input;
	}
	
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
			
		}
		catch(NumberFormatException e) {
			throw new DbException(input + "is not a valid decimal number.");
			
		}
	}
	
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the enter key to quit:");
		
		operations.forEach(line -> System.out.println("  " + line));
		
		if(Objects.isNull(curProject) ) {
			System.out.println("\nYou are not working with project.");
		} else {
			System.out.println("\nYou are working with project:" + curProject);
		}
		
	}
	private void deleteProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter the ID of the project to delete.");
		
		projectService.deleteProject(projectId);
		System.out.println("Project " + projectId + " was deleted successfully.");
		
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId));
		curProject = null;
	}
	
	private void updateProjectDetails() {
		if(Objects.nonNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		
		String projectName = 
				getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
	
		BigDecimal estimatedHours = 
				getDecimalInput("Enter the estimated hours [" + curProject.getProjectName() + "]");
	
		BigDecimal actualHours = 
				getDecimalInput("Enter the actual hours [" + curProject.getProjectName() + "]");
	
		Integer difficulty = 
				getIntInput("Enter the project difficulty (1-5) [" + curProject.getProjectName() + "]");
	
		String notes = 
				getStringInput("Enter the project notes [" + curProject.getProjectName() + "]");
	
		Project project = new Project();
		
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		projectService.modifyProjectDetails(project);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}
}
