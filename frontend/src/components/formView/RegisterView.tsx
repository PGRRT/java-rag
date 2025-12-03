import ContentWrapper from "@/components/ui/ContentWrapper";
import Input from "@/components/ui/Input";
import Link from "@/components/ui/LinkRenderer";
import { typography } from "@/constants/typography";
import type { FieldErrors, UseFormRegister } from "react-hook-form";
import type { RegisterFormData } from "@/api/schemas/registerSchema";
import { cx } from "@emotion/css";

const RegisterView = ({
  register,
  errors,
  children,
}: {
  register: UseFormRegister<RegisterFormData>;
  errors: FieldErrors<RegisterFormData>;
  children?: React.ReactNode;
}) => {
  return (
    <>
      <h4 className={cx("text-center", typography.textXl)}>
        Create your account
      </h4>

      <ContentWrapper direction="column" gap="30px">
        <Input
          label="Email"
          type="email"
          autoComplete="email"
          {...register("email")}
          error={errors.email?.message}
        />

        <Input
          label="Password"
          type="password"
          description="Password must be at least 6 characters with letters and numbers."
          autoComplete="new-password"
          error={errors.password?.message}
          {...register("password")}
        />

        <Input
          label="Confirm Password"
          type="password"
          description="Please re-enter your password."
          autoComplete="new-password"
          error={errors.confirmPassword?.message}
          {...register("confirmPassword")}
        />

        {children}
      </ContentWrapper>
      <ContentWrapper gap="10px" customCss={cx("center")}>
        <span className={typography.textM}>Already have an account?</span>
        <Link className={typography.textM} href="/sign-in" includeLinkStyles>
          Log in
        </Link>
      </ContentWrapper>
    </>
  );
};

export default RegisterView;
