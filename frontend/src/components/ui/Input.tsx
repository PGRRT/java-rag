import type { ChangeEvent, InputHTMLAttributes } from "react";
import ContentWrapper from "@/components/ui/ContentWrapper";
import { typography } from "@/constants/typography";
import { colorPalette } from "@/constants/colorPalette";
import { cx, css } from "@emotion/css";
import { styles } from "@/constants/styles";

interface InputProps
  extends Omit<InputHTMLAttributes<HTMLInputElement>, "onChange"> {
  value?: string;
  onChange?: (e: ChangeEvent<HTMLInputElement>) => void;
  label?: string;
  error?: string;
  description?: string;
  customCss?: any;
}

const Input: React.FC<InputProps> = ({
  value,
  onChange,
  label,
  error,
  customCss,
  description,
  ...props
}) => {

  return (
    <ContentWrapper gap="10px" direction="column">
      {label && (
        <label htmlFor={props.name} className={cx(typography.textM, css``)}>
          {label}
        </label>
      )}
      <input
        aria-invalid={error ? "true" : "false"}
        id={props.name}
        value={value}
        onChange={onChange}
        {...props}
        className={cx(
          css`
            height: 40px;
            padding: 8px 16px;
            border: 1px solid ${error ? "#ef4444" : colorPalette.strokePrimary};
            border-radius: ${styles.borderRadius.medium};
            background-color: ${colorPalette.background};
            color: ${colorPalette.text};
            font-size: 14px;
            outline: none;
            transition: all 0.2s;

            &:focus {
              border-color: ${colorPalette.primary};
              box-shadow: 0 0 0 2px ${colorPalette.primary}33;
            }

            &:disabled {
              background-color: ${colorPalette.backgroundSecondary};
              cursor: not-allowed;
              opacity: 0.6;
            }

            &::placeholder {
              color: ${colorPalette.textMuted};
            }
          `,
          customCss
        )}
      />
      {description && (
        <p
          className={cx(
            typography.textS,
            css`
              color: ${colorPalette.textMuted};
            `
          )}
        >
          {description}
        </p>
      )}
      {error && error?.trim()?.length > 0 && (
        <p
          className={css`
            color: #ef4444;
            font-size: 12px;
          `}
        >
          {error}
        </p>
      )}
    </ContentWrapper>
  );
};

export default Input;
