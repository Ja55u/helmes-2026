import type { FieldErrors, Sector, SubmissionData } from './types'

export class ValidationError extends Error {
  readonly errors: FieldErrors

  constructor(errors: FieldErrors) {
    super('Validation failed')
    this.errors = errors
  }
}

export async function fetchSectors(): Promise<Sector[]> {
  const res = await fetch('/api/sectors')
  if (!res.ok) {
    throw new Error(`Failed to load sectors (${res.status})`)
  }
  return res.json()
}

export async function fetchSubmission(): Promise<SubmissionData | null> {
  const res = await fetch('/api/submission')
  if (res.status === 204) {
    return null
  }
  if (!res.ok) {
    throw new Error(`Failed to load submission (${res.status})`)
  }
  return res.json()
}

export async function saveSubmission(data: SubmissionData): Promise<SubmissionData> {
  const res = await fetch('/api/submission', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  if (res.status === 400) {
    const body = await res.json()
    throw new ValidationError(body.errors ?? {})
  }
  if (!res.ok) {
    throw new Error(`Failed to save submission (${res.status})`)
  }
  return res.json()
}
