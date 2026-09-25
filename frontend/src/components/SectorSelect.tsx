import { forwardRef, useMemo } from 'react'
import type { Sector } from '../types'

interface SectorSelectProps {
  id: string
  sectors: Sector[]
  value: number[]
  onChange: (ids: number[]) => void
  onBlur: () => void
  'aria-invalid'?: boolean
  'aria-describedby'?: string
}

const SectorSelect = forwardRef<HTMLSelectElement, SectorSelectProps>(function SectorSelect(
  { id, sectors, value, onChange, onBlur, ...aria },
  ref,
) {
  const paths = useMemo(() => {
    const map = new Map<number, string>()
    const ancestorNames: string[] = []
    for (const sector of sectors) {
      ancestorNames[sector.depth] = sector.name
      const pathSoFar = ancestorNames.slice(0, sector.depth + 1)
      map.set(sector.id, pathSoFar.join(' › '))
    }
    return map
  }, [sectors])

  return (
    <select
      id={id}
      multiple
      size={10}
      ref={ref}
      value={value.map(String)}
      onChange={e => onChange(Array.from(e.target.selectedOptions, o => Number(o.value)))}
      onBlur={onBlur}
      {...aria}
    >
      {sectors.map(s => (
        <option key={s.id} value={s.id} title={paths.get(s.id)}>
          {' '.repeat(s.depth * 4) + s.name}
        </option>
      ))}
    </select>
  )
})

export default SectorSelect
