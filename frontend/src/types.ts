export interface Sector {
  id: number
  name: string
  depth: number
}

export interface SubmissionData {
  name: string
  sectorIds: number[]
  agreeToTerms: boolean
}

export type FieldErrors = Partial<Record<keyof SubmissionData, string>>
