import type { components } from '../api/models/schema';

export type LaboratoryWork = components["schemas"]["LaboratoryWork"];
export type Task = components["schemas"]["Task"];

// Mock data for initial development
const MOCK_LABS: LaboratoryWork[] = Array.from({ length: 10 }, (_, i) => ({
  id: i + 1,
  number: i + 1,
  topic: `Lab Work ${i + 1}`,
  description: `Description for lab ${i + 1}`,
  tasks: [{ id: 100 + i, number: 1, description: "Basic task" }]
}));

const MOCK_SUBMISSIONS: any[] = [
  { id: 101, labNumber: 1, grade: 5, feedback: "Отличная работа!", student: { firstName: 'Иван', lastName: 'Иванов' }, task: { description: 'Initialize a repository and make the first commit.' } },
  { id: 102, labNumber: 2, grade: 4, feedback: "Хорошо, но есть замечания.", student: { firstName: 'Иван', lastName: 'Иванов' }, task: { description: 'Create a new branch and merge it back to main.' } },
  { id: 201, labNumber: 1, grade: 3, feedback: "Требуется доработка.", student: { firstName: 'Петр', lastName: 'Петров' }, task: { description: 'Initialize a repository and make the first commit.' } }
];

export const labService = {
  async getAllLabs(): Promise<LaboratoryWork[]> {
    return Promise.resolve(MOCK_LABS);
  },

  async getLabById(id: number): Promise<LaboratoryWork | undefined> {
    const lab = MOCK_LABS.find(l => l.id === id);
    return Promise.resolve(lab);
  },

  async createLab(lab: LaboratoryWork): Promise<LaboratoryWork> {
    return Promise.resolve({ ...lab, id: Date.now() });
  },

  async updateLab(id: number, lab: LaboratoryWork): Promise<LaboratoryWork> {
    return Promise.resolve(lab);
  },

  async deleteLab(id: number): Promise<void> {
    return Promise.resolve();
  },

  async createTask(labId: number, task: Task): Promise<Task> {
    return Promise.resolve({ ...task, id: Date.now() });
  },

  async getTaskById(taskId: number): Promise<Task | undefined> {
    for (const lab of MOCK_LABS) {
      const task = lab.tasks?.find(t => t.id === taskId);
      if (task) return Promise.resolve(task);
    }
    return undefined;
  },

  async updateTask(taskId: number, task: Task): Promise<Task> {
    return Promise.resolve(task);
  },

  async getAllStudents(): Promise<components["schemas"]["UserResponseDto"][]> {
    return Promise.resolve([
      { 
        id: 1, 
        username: 'student1', 
        firstName: 'Иван',
        lastName: 'Иванов',
        submissions: [
          { labNumber: 1, grade: 5, submissionId: 101, exists: true },
          { labNumber: 3, grade: 4, submissionId: 102, exists: true }
        ]
      },
      { 
        id: 2, 
        username: 'student2', 
        firstName: 'Петр',
        lastName: 'Петров',
        submissions: [
          { labNumber: 1, grade: 3, submissionId: 201, exists: true },
          { labNumber: 5, grade: undefined, submissionId: 202, exists: false }
        ]
      }
    ]);
  },

  async createStudent(student: components["schemas"]["UserCreateDto"]): Promise<components["schemas"]["UserResponseDto"]> {
    console.log('Mock create student:', student);
    return Promise.resolve({ id: Date.now(), username: student.username, firstName: student.firstName, lastName: student.lastName });
  },

  async deleteStudent(id: number): Promise<void> {
    console.log('Mock delete student:', id);
    return Promise.resolve();
  },

  async assignTask(taskId: number, studentId: number): Promise<void> {
    return Promise.resolve();
  },

  async getSubmissionById(id: number): Promise<any> {
    const submission = MOCK_SUBMISSIONS.find(s => s.id === id);
    return Promise.resolve(submission);
  },

  async gradeSubmission(submissionId: number, grade: number, feedback: string): Promise<void> {
    console.log(`Mock grade submission ${submissionId}: ${grade}, feedback: ${feedback}`);
    return Promise.resolve();
  },

  async checkSubmission(submissionId: number, params: { reportType: string }): Promise<any> {
    console.log(`Mock check submission ${submissionId} with type ${params.reportType}`);
    
    const mockPath = params.reportType  === 'TWO_GRAPH' ? '/mock/two_graph.json' : '/mock/merged_graph.json';
    const response = await fetch(mockPath);
    
    if (!response.ok) {
        throw new Error(`Failed to load mock data: ${response.statusText}`);
    }

    const mockData = await response.json();
  
    if (params.reportType === 'TWO_GRAPH') {
      return {
        type: 'TwoGraphComparisonResultDto' as const,
        firstGraph: mockData.first_graph,
        secondGraph: mockData.second_graph,
        compareResult: {
          matchedHashes1To2: mockData.compare_result.matched_hashes_1_to_2
        }
      };
    } else {
      return {
        type: 'MergedGraphComparisonResultDto' as const,
        mergedGraph: mockData.merged_graph,
        compareResult: mockData.compare_result
      };
    }
  },

  async checkSolution(labId: number, params: { reportType: string }): Promise<any> {
    return this.checkSubmission(labId, params);
  },

  async getStudentLabs(): Promise<components["schemas"]["LaboratoryWorkDto"][]> {
    return Promise.resolve([
      { id: 1, number: 1, topic: "Git Basics", maxGrade: 5, tasks: [{ id: 1, number: 1, description: "Init repo", status: "COMPLETED", grade: 5 }] },
      { id: 2, number: 2, topic: "Branching", maxGrade: 5, tasks: [{ id: 2, number: 1, description: "Merge branch", status: "PENDING", grade: 4 }] }
    ]);
  },

  async uploadSolution(labId: number, file: File): Promise<any> {
    console.log(`Mock upload for lab ${labId}: ${file.name}`);
    return Promise.resolve();
  }
};
