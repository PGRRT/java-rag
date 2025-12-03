import { typography } from "@/constants/typography";
import LinkRenderer from "@/components/ui/LinkRenderer";
import { cx } from "@emotion/css";
const AgreeFooter = () => {
  return (
    <>
      <span className={cx("text-center", typography.textS)}>
        By continuing, you agree to Med AI{" "}
        <LinkRenderer href="/terms" includeLinkStyles target="_blank">
          Terms of Service
        </LinkRenderer>{" "}
        and{" "}
        <LinkRenderer href="/privacy" includeLinkStyles target="_blank">
          Privacy Notice
        </LinkRenderer>
        .
      </span>
    </>
  );
};

export default AgreeFooter;
