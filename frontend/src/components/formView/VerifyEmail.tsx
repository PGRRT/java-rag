import ContentWrapper from "@/components/ui/ContentWrapper";
import Input from "@/components/ui/Input";
import { typography } from "@/constants/typography";
import type { FieldErrors, UseFormRegister } from "react-hook-form";
import type { RegisterFormData } from "@/api/schemas/registerSchema";
import { useTranslation, Trans } from "react-i18next";

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
  const { t } = useTranslation();

  return (
    <>
      <h4 className={typography.textXl}>{t("auth.verifyEmail")}</h4>
      <ContentWrapper direction="column" gap="15px">
        <span className={typography.textS}>
          <Trans i18nKey="auth.verifyEmailDescription" values={{ email }}>
            We've emailed a one time security code to{" "}
            <strong>{{ email }}</strong>, please enter the code below
          </Trans>
        </span>
      </ContentWrapper>

      <ContentWrapper direction="column" gap="25px">
        <Input
          label={t("auth.verificationCode")}
          type="text"
          placeholder={t("auth.verificationCodePlaceholder")}
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
