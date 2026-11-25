import type { VariantProps } from "class-variance-authority"
import { Link, type LinkProps } from "react-router-dom"
import { buttonVariants } from "@/components/ui/button"
import { cn } from "@/lib/utils"

/**
 * A router link that looks like a button. Navigation stays a real `<a>` with
 * link semantics; use `Button` only for actions.
 */
function ButtonLink({
  className,
  variant = "default",
  size = "default",
  ...props
}: LinkProps & VariantProps<typeof buttonVariants>) {
  return (
    <Link
      data-slot="button-link"
      className={cn(buttonVariants({ variant, size }), className)}
      {...props}
    />
  )
}

export { ButtonLink }
