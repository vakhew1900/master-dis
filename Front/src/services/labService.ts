import type { components } from '../api/models/schema';

export type LaboratoryWork = components["schemas"]["LaboratoryWork"];
export type Task = components["schemas"]["Task"];

// Mock data for initial development
const MOCK_LABS: LaboratoryWork[] = [
  {
    id: 1,
    number: 1,
    topic: "Introduction to Git",
    description: "In this laboratory work, you will learn the basics of Git version control system, including initialization, staging, and committing changes. We will also cover branching and basic merging techniques to manage different versions of your project efficiently.",
    tasks: [
      { id: 101, number: 1, description: "Initialize a repository and make the first commit." },
      { id: 102, number: 2, description: "Create a new branch and merge it back to main." }
    ]
  },
  {
    id: 2,
    number: 2,
    topic: "Advanced Branching",
    description: "Deep dive into git branching strategies. This lab covers rebasing, cherry-picking, and resolving complex merge conflicts. You will learn how to maintain a clean project history and work effectively in a team environment.",
    tasks: [
      { id: 201, number: 1, description: "Resolve a merge conflict between two feature branches." },
      { id: 202, number: 2, description: "Use rebase to clean up your commit history." },
      { id: 203, number: 3, description: "Cherry-pick a specific commit from one branch to another." }
    ]
  },
  {
    id: 3,
    number: 3,
    topic: "Git Internals",
    description: "Understanding how Git stores data internally. Objects, refs, and the index. We will explore the .git directory and learn how to use plumbing commands to inspect the repository's internal state.",
    tasks: [
      { id: 301, number: 1, description: "Inspect git object database using cat-file." }
    ]
  }
];

export const labService = {
  // Get all labs
  async getAllLabs(): Promise<LaboratoryWork[]> {
    return Promise.resolve(MOCK_LABS);
  },

  // Get lab by ID
  async getLabById(id: number): Promise<LaboratoryWork | undefined> {
    const lab = MOCK_LABS.find(l => l.id === id);
    return Promise.resolve(lab);
  },

  // Stub for creating a lab
  async createLab(lab: LaboratoryWork): Promise<LaboratoryWork> {
    console.log('Mock create lab:', lab);
    return Promise.resolve({ ...lab, id: Date.now() });
  },

  // Stub for updating a lab
  async updateLab(id: number, lab: LaboratoryWork): Promise<LaboratoryWork> {
    console.log('Mock update lab:', id, lab);
    return Promise.resolve(lab);
  },

  // Stub for deleting a lab
  async deleteLab(id: number): Promise<void> {
    console.log('Mock delete lab:', id);
    return Promise.resolve();
  },

  // Stub for adding a task to a lab
  async createTask(labId: number, task: Task): Promise<Task> {
    console.log('Mock create task for lab:', labId, task);
    return Promise.resolve({ ...task, id: Date.now() });
  }
};
