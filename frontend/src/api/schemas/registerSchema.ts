import * as z from "zod";

export const registerSchema = z
  .object({
    email: z.email({ message: "Invalid email address" }),
    password: z.string()
      .min(6, { message: "Password must be at least 6 characters" })
      .regex(/[a-zA-Z]/, { message: "Password must include a letter" })
      .regex(/[0-9]/, { message: "Password must include a number" }),

    confirmPassword: z.string(),
    otp: z.string().optional(), // one time password (verification code)
  })
  .refine((data) => data.password == data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"], // error will be shown on confirmPassword field
  }
);

export type RegisterFormData = z.infer<typeof registerSchema>;

