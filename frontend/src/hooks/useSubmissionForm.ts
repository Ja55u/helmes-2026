import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { fetchSectors, fetchSubmission, saveSubmission, ValidationError } from '../api'
import type { SubmissionData } from '../types'
import { submissionSchema } from '../validation'

export function useSubmissionForm() {
  const [message, setMessage] = useState<string | null>(null)
  const queryClient = useQueryClient()

  const sectorsQuery = useQuery({ queryKey: ['sectors'], queryFn: fetchSectors })
  const submissionQuery = useQuery({ queryKey: ['submission'], queryFn: fetchSubmission })

  const form = useForm<SubmissionData>({
    resolver: zodResolver(submissionSchema),
    defaultValues: { name: '', sectorIds: [], agreeToTerms: false },
  })
  const { reset, setError, handleSubmit } = form

  useEffect(() => {
    if (submissionQuery.data) {
      reset(submissionQuery.data)
    }
  }, [submissionQuery.data, reset])

  const mutation = useMutation({
    mutationFn: saveSubmission,
    onSuccess: data => {
      reset(data)
      queryClient.setQueryData(['submission'], data)
      setMessage('Saved. You can keep editing and save again.')
    },
    onError: err => {
      if (err instanceof ValidationError) {
        for (const [field, fieldMessage] of Object.entries(err.errors)) {
          setError(field as keyof SubmissionData, { message: fieldMessage })
        }
      } else {
        setMessage('Could not save. Please try again.')
      }
    },
  })

  const onSubmit = handleSubmit(data => {
    setMessage(null)
    mutation.mutate(data)
  })

  return {
    form,
    sectors: sectorsQuery.data ?? [],
    isLoading: sectorsQuery.isPending || submissionQuery.isPending,
    isError: sectorsQuery.isError || submissionQuery.isError,
    isSaving: mutation.isPending,
    message,
    onSubmit,
  }
}
