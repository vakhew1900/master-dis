import { getOpenAPIDefinition } from '../api/generated/jserver';
import type { 
  LaboratoryWork, 
  Task, 
  UserResponseDto, 
  UserCreateDto, 
  LaboratoryWorkDto,
  StudentSubmission
} from '../api/generated/model';

const api = getOpenAPIDefinition();

export type { LaboratoryWork, Task, UserResponseDto, StudentSubmission };

export const labService = {
  async getAllLabs(): Promise<LaboratoryWork[]> {
    const data = await api.getAllLabs();
    console.log('labService.getAllLabs data:', data);
    return data;
  },

  async getLabById(id: number): Promise<LaboratoryWork> {
    return await api.getLabById(id);
  },

  async createLab(lab: LaboratoryWork): Promise<LaboratoryWork> {
    return await api.createLab(lab);
  },

  async updateLab(id: number, lab: LaboratoryWork): Promise<LaboratoryWork> {
    return await api.updateLab(id, lab);
  },

  async deleteLab(id: number): Promise<void> {
    return await api.deleteLab(id);
  },

  async createTask(labId: number, task: { number: number, description: string }, file?: File): Promise<Task> {
    return await api.createTask(
      { labId: labId, number: task.number, description: task.description },
      file ? { file: file as any } : undefined
    );
  },

  async getTaskById(taskId: number): Promise<Task> {
    return await api.getTaskById(taskId);
  },

  async updateTask(taskId: number, task: { number?: number, description?: string }, file?: File): Promise<Task> {
    return await api.updateTask(
      taskId,
      file ? { file: file as any } : undefined,
      { number: task.number, description: task.description }
    );
  },

  async getAllStudents(): Promise<UserResponseDto[]> {
    return await api.getAllStudents();
  },

  async createStudent(student: UserCreateDto): Promise<UserResponseDto> {
    return await api.createStudent(student);
  },

  async deleteStudent(id: number): Promise<void> {
    return await api.deleteUser(id);
  },

  async assignTask(taskId: number, studentId: number): Promise<StudentSubmission> {
    return await api.assignTask(studentId, taskId);
  },

  async getSubmissionById(id: number): Promise<StudentSubmission> {
    // There is no direct getSubmissionById in the schema, but we have getStudentSubmissions or getAllSubmissions.
    // Based on the pages, we might need to filter.
    const submissions = await api.getAllSubmissions();
    const found = submissions.find(s => s.id === id);
    if (!found) throw new Error("Submission not found");
    return found;
  },

  async gradeSubmission(submissionId: number, grade: number, feedback: string): Promise<StudentSubmission> {
    return await api.gradeSubmission(submissionId, { grade, feedback });
  },

  async getStudentLabs(): Promise<LaboratoryWorkDto[]> {
    return await api.getLabs();
  },

  async uploadSolution(taskId: number, file: File): Promise<StudentSubmission> {
    return await api.uploadSolution(taskId, { file });
  }
};
