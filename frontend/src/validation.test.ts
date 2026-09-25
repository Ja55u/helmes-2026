import { describe, expect, it } from 'vitest'
import { validate } from './validation'

describe('validate', () => {
  it('returns all three messages for empty data', () => {
    const errors = validate({ name: '', sectorIds: [], agreeToTerms: false })
    expect(errors.name).toBe('Name is required')
    expect(errors.sectorIds).toBe('Select at least one sector')
    expect(errors.agreeToTerms).toBe('You must agree to the terms')
  })

  it('treats a whitespace-only name as required', () => {
    const errors = validate({ name: '   ', sectorIds: [1], agreeToTerms: true })
    expect(errors.name).toBe('Name is required')
  })

  it('rejects a name over 100 characters', () => {
    const errors = validate({ name: 'a'.repeat(101), sectorIds: [1], agreeToTerms: true })
    expect(errors.name).toBe('Name must be at most 100 characters')
  })

  it('accepts a name of exactly 100 characters', () => {
    const errors = validate({ name: 'a'.repeat(100), sectorIds: [1], agreeToTerms: true })
    expect(errors.name).toBeUndefined()
  })

  it('returns no errors for valid data', () => {
    const errors = validate({ name: 'Alice', sectorIds: [1], agreeToTerms: true })
    expect(errors).toEqual({})
  })
})
