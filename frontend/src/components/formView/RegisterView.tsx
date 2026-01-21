import ContentWrapper from "@/components/ui/ContentWrapper";
import Input from "@/components/ui/Input";
import Link from "@/components/ui/LinkRenderer";
import { typography } from "@/constants/typography";
import type { FieldErrors, UseFormRegister } from "react-hook-form";
import type { RegisterFormData } from "@/api/schemas/registerSchema";
import { cx } from "@emotion/css";
import { useTranslation } from "react-i18next";

const RegisterView = ({
  register,
  errors,
  children,
}: {
  register: UseFormRegister<RegisterFormData>;
  errors: FieldErrors<RegisterFormData>;
  children?: React.ReactNode;
}) => {
  const { t } = useTranslation();

  return (
    <>
      <h4 className={cx("text-center", typography.textXl)}>
        {t("auth.register")}
      </h4>

      <ContentWrapper direction="column" gap="30px">
        <Input
          label={t("auth.email")}
          type="email"
          autoComplete="email"
          {...register("email")}
          error={errors.email?.message}
        />

        <Input
          label={t("auth.password")}
          type="password"
          description={t("auth.passwordRequirements")}
          autoComplete="new-password"
          error={errors.password?.message}
          {...register("password")}
        />

        <Input
          label={t("auth.confirmPassword")}
          type="password"
          description={t("auth.passwordConfirmDescription")}
          autoComplete="new-password"
          error={errors.confirmPassword?.message}
          {...register("confirmPassword")}
        />

        {children}
      </ContentWrapper>
      <ContentWrapper gap="10px" customCss={cx("center")}>
        <span className={typography.textM}>
          {t("auth.alreadyHaveAccount")}
        </span>
        <Link className={typography.textM} href="/sign-in" includeLinkStyles>
          {t("auth.login")}
        </Link>
      </ContentWrapper>
    </>
  );
};

export default RegisterView;
