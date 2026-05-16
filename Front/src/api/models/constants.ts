export const SEVERITY = {
  IDENTICAL: 'IDENTICAL',
  MODIFIED: 'MODIFIED',
  EXTRA: 'EXTRA',
  MOVABLE: 'MOVABLE',
  // UI-specific variations
  MOVABLE_STUDENT: 'MOVABLE_STUDENT',
  MOVABLE_REFERENCE: 'MOVABLE_REFERENCE',
} as const;

export const USER_ROLES = {
  ADMIN: 'ADMIN',
  STUDENT: 'STUDENT',
} as const;

export type UserRole = typeof USER_ROLES[keyof typeof USER_ROLES];

export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  COMPARISON: '/comparison',
  COMPARISON_RESULT: '/comparison-result',
  ADMIN: {
    ROOT: 'admin',
    LABS: {
      ROOT: 'labs',
      NEW: 'new',
      DETAIL: (id: string | number) => `${id}`,
    },
    TASKS: {
      NEW: 'tasks/new',
      EDIT: (id: string | number) => `tasks/${id}/edit`,
    }
  }
} as const;

export const SEVERITY_COLORS = {
  [SEVERITY.IDENTICAL]: { bg: '#365939', border: '#496c4b' },
  [SEVERITY.MODIFIED]: { bg: '#5e5339', border: '#80714a' },
  [SEVERITY.EXTRA]: { bg: '#593939', border: '#804b4b' },
  [SEVERITY.MOVABLE_STUDENT]: { bg: '#384c67', border: '#4b6a8e' },
  [SEVERITY.MOVABLE_REFERENCE]: { bg: '#4e3867', border: '#6a4b8e' },
  DEFAULT: { bg: '#2b2b2b', border: '#3f3f3f' }
} as const;
