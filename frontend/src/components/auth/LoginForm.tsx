import ContentWrapper from "@/components/ui/ContentWrapper";
import Input from "@/components/ui/Input";
import Link from "@/components/ui/LinkRenderer";
import { Button } from "@mantine/core";
import { typography } from "@/constants/typography";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { loginSchema } from "@/api/schemas/loginSchema";
import type { LoginFormData } from "@/api/schemas/loginSchema";
import type { Credentials } from "@/types/user";
import { css, cx } from "@emotion/css";
import { showAsyncToastAndRedirect, showToast } from "@/utils/showToast";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import AgreeFooter from "@/components/AgreeFooter";
import { useTranslation } from "react-i18next";

const LoginForm = () => {
  const { t } = useTranslation();
  const { login: loginUser, clearAuthError, user, isLoading } = useAuth();
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    setError,
    clearErrors,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  watch((_, { name }) => {
    if (name === "email" || name === "password") {
      clearErrors(name);
    }
  });

  const onSubmit = async (formData: LoginFormData) => {
    const userData: Credentials = {
      email: formData.email,
      password: formData.password,
    };

    clearAuthError();
    const { data, error } = await loginUser(userData);

    console.log("data, error", data, error);

    if (error) {
      setError("email", { type: "manual", message: " " }); // to highlight both fields
      setError("password", { type: "manual", message: error });

      return;
    }
    // // this is used to make isSubmitting true after the user is logged in (so that the button is disabled while redirecting)
    // await showAsyncToastAndRedirect(
    //   t("auth.loginSuccess"),
    //   "/",
    //   2000,
    //   navigate,
    // );
  };

  return (
    <ContentWrapper
      width="100%"
      padding="20px"
      height="100%"
      direction="column"
      justify="space-between"
      align="center"
      customCss={css``}
    >
      <div /> {/* Spacer */}
      <ContentWrapper
        width="100%"
        maxWidth="400px"
        direction="column"
        gap="30px"
      >
        <h4 className={cx(typography.textXl, "center")}>
          {t("auth.loginTitle")}
        </h4>

        <form onSubmit={handleSubmit(onSubmit)}>
          <ContentWrapper direction="column" gap="30px">
            <Input
              label={t("auth.email")}
              type="email"
              {...register("email")}
              error={errors.email?.message}
              onChange={() => {
                clearErrors();
              }}
            />

            <Input
              label={t("auth.password")}
              type="password"
              {...register("password")}
              error={errors.password?.message}
              onChange={() => {
                clearErrors();
              }}
            />

            <Button type="submit" disabled={isSubmitting || isLoading}>
              {t("auth.login")}
            </Button>
          </ContentWrapper>
        </form>
        <ContentWrapper gap="10px" customCss={cx("center")}>
          <span className={typography.textM}>{t("auth.dontHaveAccount")}</span>
          <Link className={typography.textM} href="/sign-up" includeLinkStyles>
            {t("auth.signUp")}
          </Link>
        </ContentWrapper>
      </ContentWrapper>
      <AgreeFooter />
    </ContentWrapper>
  );
};

export default LoginForm;
