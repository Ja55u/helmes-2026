import { Controller } from 'react-hook-form'
import { useSubmissionForm } from '../hooks/useSubmissionForm'
import SectorSelect from './SectorSelect'
import './SectorForm.css'

function SectorForm() {
  const { form, sectors, isLoading, isError, isSaving, message, onSubmit } = useSubmissionForm()
  const {
    register,
    control,
    formState: { errors },
  } = form

  if (isLoading) {
    return (
      <main>
        <h1>Sectors</h1>
        <p>Loading…</p>
      </main>
    )
  }

  if (isError) {
    return (
      <main>
        <h1>Sectors</h1>
        <p role="status">Could not load the form. Please refresh the page.</p>
      </main>
    )
  }

  return (
    <main>
      <h1>Sectors</h1>
      <p>Please enter your name and pick the Sectors you are currently involved in.</p>

      <form noValidate onSubmit={onSubmit}>
        <div className="field">
          <label htmlFor="name">Name</label>
          <input
            id="name"
            type="text"
            maxLength={100}
            autoComplete="name"
            aria-invalid={errors.name ? true : undefined}
            aria-describedby={errors.name ? 'name-error' : undefined}
            {...register('name')}
          />
          {errors.name && (
            <p id="name-error" className="error">
              {errors.name.message}
            </p>
          )}
        </div>

        <div className="field">
          <label htmlFor="sectors">Sectors</label>
          <Controller
            name="sectorIds"
            control={control}
            render={({ field }) => (
              <SectorSelect
                id="sectors"
                sectors={sectors}
                value={field.value}
                onChange={field.onChange}
                onBlur={field.onBlur}
                ref={field.ref}
                aria-invalid={errors.sectorIds ? true : undefined}
                aria-describedby={errors.sectorIds ? 'sectors-hint sectors-error' : 'sectors-hint'}
              />
            )}
          />
          <p id="sectors-hint" className="hint">
            Hold Ctrl (Cmd on Mac) to select multiple sectors.
          </p>
          {errors.sectorIds && (
            <p id="sectors-error" className="error">
              {errors.sectorIds.message}
            </p>
          )}
        </div>

        <div className="field">
          <label>
            <input
              type="checkbox"
              aria-invalid={errors.agreeToTerms ? true : undefined}
              aria-describedby={errors.agreeToTerms ? 'terms-error' : undefined}
              {...register('agreeToTerms')}
            />{' '}
            Agree to terms
          </label>
          {errors.agreeToTerms && (
            <p id="terms-error" className="error">
              {errors.agreeToTerms.message}
            </p>
          )}
        </div>

        <button type="submit" disabled={isSaving}>
          {isSaving ? 'Saving…' : 'Save'}
        </button>

        {message && <p role="status">{message}</p>}
      </form>
    </main>
  )
}

export default SectorForm
