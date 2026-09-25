import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { fetchSectors, fetchSubmission, saveSubmission, ValidationError } from '../api'
import type { Sector, SubmissionData } from '../types'
import SectorForm from './SectorForm'

vi.mock('../api', async importOriginal => {
  const actual = await importOriginal<typeof import('../api')>()
  return { ...actual, fetchSectors: vi.fn(), fetchSubmission: vi.fn(), saveSubmission: vi.fn() }
})

const sectors: Sector[] = [
  { id: 1, name: 'Manufacturing', depth: 0 },
  { id: 6, name: 'Food and Beverage', depth: 1 },
  { id: 437, name: 'Other', depth: 2 },
  { id: 2, name: 'Service', depth: 0 },
]

function mockLoad(submission: SubmissionData | null = null) {
  vi.mocked(fetchSectors).mockResolvedValue(sectors)
  vi.mocked(fetchSubmission).mockResolvedValue(submission)
}

function renderForm() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(
    <QueryClientProvider client={queryClient}>
      <SectorForm />
    </QueryClientProvider>,
  )
}

describe('SectorForm', () => {
  it('renders sector options from the API', async () => {
    mockLoad()
    renderForm()

    const foodOption = (await screen.findByRole('option', {
      name: /Food and Beverage/,
    })) as HTMLOptionElement
    expect(foodOption.textContent).toMatch(/^ {4}Food and Beverage/)

    const otherOption = screen.getByRole('option', { name: 'Other' })
    expect(otherOption).toHaveAttribute('title', 'Manufacturing › Food and Beverage › Other')
  })

  it('shows all three errors and does not save when submitting empty', async () => {
    mockLoad()
    const user = userEvent.setup()
    renderForm()

    await user.click(await screen.findByRole('button', { name: 'Save' }))

    expect(await screen.findByText('Name is required')).toBeInTheDocument()
    expect(screen.getByText('Select at least one sector')).toBeInTheDocument()
    expect(screen.getByText('You must agree to the terms')).toBeInTheDocument()
    expect(saveSubmission).not.toHaveBeenCalled()
  })

  it('prefills the form from a stored submission', async () => {
    mockLoad({ name: 'Alice', sectorIds: [1, 6], agreeToTerms: true })
    renderForm()

    const nameInput = (await screen.findByLabelText('Name')) as HTMLInputElement
    expect(nameInput.value).toBe('Alice')
    expect((screen.getByRole('checkbox') as HTMLInputElement).checked).toBe(true)

    const manufacturingOption = screen.getByRole('option', {
      name: /Manufacturing/,
    }) as HTMLOptionElement
    expect(manufacturingOption.selected).toBe(true)
  })

  it('submits valid data and refills the form from the response', async () => {
    mockLoad()
    vi.mocked(saveSubmission).mockResolvedValue({ name: 'Bob', sectorIds: [1], agreeToTerms: true })
    const user = userEvent.setup()
    renderForm()

    await user.type(await screen.findByLabelText('Name'), '  Bob  ')
    await user.selectOptions(screen.getByRole('listbox'), ['1'])
    await user.click(screen.getByRole('checkbox'))
    await user.click(screen.getByRole('button', { name: 'Save' }))

    expect(saveSubmission).toHaveBeenCalledWith(
      { name: '  Bob  ', sectorIds: [1], agreeToTerms: true },
      expect.anything(),
    )
    expect(await screen.findByText('Saved. You can keep editing and save again.')).toBeInTheDocument()
    expect((screen.getByLabelText('Name') as HTMLInputElement).value).toBe('Bob')
  })

  it('shows a server validation error returned by save', async () => {
    mockLoad()
    vi.mocked(saveSubmission).mockRejectedValue(new ValidationError({ name: 'Name is required' }))
    const user = userEvent.setup()
    renderForm()

    await user.type(await screen.findByLabelText('Name'), 'Alice')
    await user.selectOptions(screen.getByRole('listbox'), ['1'])
    await user.click(screen.getByRole('checkbox'))
    await user.click(screen.getByRole('button', { name: 'Save' }))

    expect(await screen.findByText('Name is required')).toBeInTheDocument()
  })

  it('shows a load error when fetchSectors rejects', async () => {
    vi.mocked(fetchSectors).mockRejectedValue(new Error('network error'))
    vi.mocked(fetchSubmission).mockResolvedValue(null)
    renderForm()

    expect(
      await screen.findByText('Could not load the form. Please refresh the page.'),
    ).toBeInTheDocument()
  })
})
