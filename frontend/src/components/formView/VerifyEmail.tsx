import ContentWrapper from "@/components/ui/ContentWrapper";
import Input from "@/components/ui/Input";
import { typography } from "@/constants/typography";
import type { FieldErrors, UseFormRegister } from "react-hook-form";
import type { RegisterFormData } from "@/api/schemas/registerSchema";

const VerifyEmail = ({
  register,
  errors,
  email,
  children,
}: {
  register: UseFormRegister<RegisterFormData>;
  errors: FieldErrors<RegisterFormData>;
  email: string;
  children?: React.ReactNode;
}) => {
  return (
    <>
      <h4 className={typography.textXl}>Verify your email</h4>
      <ContentWrapper direction="column" gap="15px">
        <span className={typography.textS}>
          We've emailed a one time security code to <strong>{email}</strong>, please enter the code below
        </span>
      </ContentWrapper>

      <ContentWrapper direction="column" gap="25px">
        <Input
          label="Verification Code"
          type="text"
          placeholder="Enter 6-digit code"
          autoComplete="one-time-code"
          {...register("otp")}
          error={errors.otp?.message}
        />

        {children}
      </ContentWrapper>
    </>
  );
};

export default VerifyEmail;
