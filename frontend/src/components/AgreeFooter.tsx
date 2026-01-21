import { typography } from "@/constants/typography";
import LinkRenderer from "@/components/ui/LinkRenderer";
import { cx } from "@emotion/css";
import { Trans } from "react-i18next";

const AgreeFooter = () => {
  return (
    <>
      <span className={cx("text-center", typography.textS)}>
        <Trans
          i18nKey="auth.agreeText"
          components={{
            termsLink: (
              <LinkRenderer href="/terms" includeLinkStyles target="_blank" />
            ),
            privacyLink: (
              <LinkRenderer href="/privacy" includeLinkStyles target="_blank" />
            ),
          }}
        />
      </span>
    </>
  );
};

export default AgreeFooter;
