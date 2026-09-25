import { z } from 'zod'
import type { FieldErrors, SubmissionData } from './types'

export const submissionSchema = z.object({
  name: z
    .string()
    .refine(v => v.trim().length > 0, 'Name is required')
    .refine(v => v.trim().length <= 100, 'Name must be at most 100 characters'),
  sectorIds: z.array(z.number()).min(1, 'Select at least one sector'),
  agreeToTerms: z.boolean().refine(v => v === true, 'You must agree to the terms'),
})

export function validate(data: SubmissionData): FieldErrors {
  const result = submissionSchema.safeParse(data)
  if (result.success) return {}

  const errors: FieldErrors = {}
  for (const issue of result.error.issues) {
    const field = issue.path[0] as keyof SubmissionData
    if (!(field in errors)) errors[field] = issue.message
  }
  return errors
}
