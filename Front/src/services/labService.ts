import { getOpenAPIDefinition } from '../api/generated/jserver';
import type { 
  AdminLabDto, 
  AdminTaskDto, 
  UserResponseDto, 
  UserCreateDto, 
  StudentLabDto,
  StudentSubmission,
  AdminSubmissionDto
} from '../api/generated/model';

const api = getOpenAPIDefinition();

export type { AdminLabDto, AdminTaskDto, UserResponseDto, StudentSubmission };

export const labService = {
  async getAllLabs(): Promise<AdminLabDto[]> {
    const data = await api.getAllLabs();
    console.log('labService.getAllLabs data:', data);
    return data;
  },

  async getLabById(id: number): Promise<AdminLabDto> {
    return await api.getLabById(id);
  },

  async createLab(lab: AdminLabDto): Promise<AdminLabDto> {
    return await api.createLab(lab as any);
  },

  async updateLab(id: number, lab: AdminLabDto): Promise<AdminLabDto> {
    return await api.updateLab(id, lab as any);
  },

  async deleteLab(id: number): Promise<void> {
    return await api.deleteLab(id);
  },

  async createTask(labId: number, task: { number: number, description: string }, file?: File): Promise<AdminTaskDto> {
    return await api.createTask(
      { labId: labId, number: task.number, description: task.description },
      file ? { file: file as File } : undefined
    );
  },

  async getTaskById(taskId: number): Promise<AdminTaskDto> {
    return await api.getTaskById(taskId) as AdminTaskDto;
  },

  async updateTask(taskId: number, task: { number?: number, description?: string }, file?: File): Promise<AdminTaskDto> {
    return await api.updateTask(
      taskId,
      file ? { file: file as any } : undefined,
      { number: task.number, description: task.description }
    ) as AdminTaskDto;
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

  async assignTask(taskId: number, studentId: number): Promise<AdminSubmissionDto> {
    return await api.assignTask(studentId, taskId);
  },

  async getSubmissionById(id: number): Promise<AdminSubmissionDto> {
    // There is no direct getSubmissionById in the schema, but we have getStudentSubmissions or getAllSubmissions.
    // Based on the pages, we might need to filter.
    const submissions = await api.getAllSubmissions();
    const found = submissions.find(s => s.id === id);
    if (!found) throw new Error("Submission not found");
    return found;
  },

  async gradeSubmission(submissionId: number, grade: number, feedback: string): Promise<AdminSubmissionDto> {
    return await api.gradeSubmission(submissionId, { grade, feedback });
  },

  async getStudentLabs(): Promise<StudentLabDto[]> {
    return await api.getLabs();
  },

  async getStudentLabById(id: number): Promise<StudentLabDto | undefined> {
    const labs = await api.getLabs();
    return labs.find(l => l.id === id);
  },

  async uploadSolution(taskId: number, file: File): Promise<StudentSubmission> {
    return await api.uploadSolution(taskId, { file });
  }
};
