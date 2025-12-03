import * as z from "zod";

export const loginSchema = z
  .object({
    email: z.email({ message: "Invalid email address" }),
    password: z
      .string()
      .min(6, { message: "Password must be at least 6 characters" })
      .regex(/[a-zA-Z]/, { message: "Password must include a letter" })
      .regex(/[0-9]/, { message: "Password must include a number" }),
  })


export type LoginFormData = z.infer<typeof loginSchema>;

