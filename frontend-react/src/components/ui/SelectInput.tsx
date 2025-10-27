import { cx, css } from "@emotion/css";
import { typography } from "@/constants/typography";
import { ChevronDown } from "lucide-react";
import IconWrapper from "@/components/ui/IconWrapper";
import { useState } from "react";
import colorPalette from "@/constants/colorPalette";
import CustomPopover from "@/components/ui/CustomPopover";
import { styles } from "@/constants/styles";
import useViewport from "@/hooks/useViewport";

interface options {
  label: string;
  value: string;
}

const SelectInput = ({
  value,
  onChange,
  options,
}: {
  value: string;
  onChange: (value: string) => void;
  options: options[];
}) => {
  const button = null;
  const [open, setOpen] = useState(false);

  const currentLabel = options?.find((option) => option.value === value)?.label;

  return (
    <CustomPopover
      open={open}
      setOpen={setOpen}
      trigger={
        <div
          ref={button}
          onClick={() => setOpen((prev) => !prev)}
          className={cx(
            `${typography.textM}`,
            css`
              display: flex;
              gap: 5px;
              align-items: center;
              height: 36px;
              cursor: pointer;
              font-weight: 500;
              color: ${colorPalette.text};
              background: transparent;

              padding: 0 ${styles.padding.small};

              user-select: none;
              border-radius: ${styles.borderRadius.medium};
              &:hover {
                background: ${colorPalette.backgroundTertiary};
              }
            `
          )}
        >
          <span
            className={css`
              text-overflow: ellipsis;
              white-space: nowrap;
              max-width: 100%;
              display: inline-block;
              overflow: hidden;
            `}
          >
            {currentLabel ?? "Classical Rag"}
          </span>
          <IconWrapper
            Icon={ChevronDown}
            // className="ml-auto"
            size={16}
          />
        </div>
      }
      content={
        <div className={css``}>
          {options?.map((option) => {
            return (
              <button
                onClick={() => {
                  onChange(option.value);
                  setOpen(false);
                }}
                className={css`
                  cursor: pointer;
                  width: 100%;
                  text-align: left;
                  color: ${colorPalette.text};
                  padding: 8px 12px;
                  border-radius: ${styles.borderRadius.small};

                  // not for last
                  &:not(:last-child) {
                    border-bottom: 1px solid ${colorPalette.strokePrimary};
                  }
                  background: transparent;

                  &:hover {
                    background: ${colorPalette.backgroundTertiary};
                  }
                `}
              >
                <span className={cx(typography.textM)}>{option.label}</span>
              </button>
            );
          })}
        </div>
      }
    />
  );
};

export default SelectInput;
