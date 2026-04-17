package com.github.mark.psxplugin

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.impl.source.tree.LeafPsiElement

class MipsAnnotator : Annotator {
    private val branches = setOf(
        "beq", "bne", "bgez", "bgezal", "bgtz", "blez", "bltz", "bltzal",
        "j", "jal", "jr", "jalr", "b", "bal", "beqz", "bnez"
    )

    private val loads = setOf(
        "lw", "lh", "lhu", "lb", "lbu", "lwc1", "ldc1", "mfc0", "mfc2", "cfc2"
    )

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is LeafPsiElement || element.elementType != MipsTokenTypes.KEYWORD) return

        val text = element.text.lowercase()

        if (branches.contains(text)) {
            checkBranchDelaySlot(element, holder)
        } else if (loads.contains(text)) {
            checkLoadDelaySlot(element, holder)
        }
    }

    private fun checkBranchDelaySlot(element: PsiElement, holder: AnnotationHolder) {
        // A branch instruction should be followed by a NOP or another instruction in the delay slot.
        // In PSX assembly, if the user doesn't know about delay slots, they might be surprised.
        // We look for the "next" logical instruction.
        
        val nextInstruction = findNextInstruction(element)
        if (nextInstruction == null) {
            holder.newAnnotation(HighlightSeverity.WARNING, "Branch/Jump instruction at the end of file without a delay slot instruction.")
                .create()
        } else if (nextInstruction.text.lowercase() != "nop") {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Instruction in branch delay slot will execute before the branch completes. In MIPS, the instruction immediately following a branch or jump is executed before the branch takes effect. Consider adding a 'nop' if this is unintentional.")
                .create()
        }
    }

    private fun checkLoadDelaySlot(element: PsiElement, holder: AnnotationHolder) {
        // Find the destination register of the load
        val destReg = findDestRegister(element) ?: return
        
        val nextInstruction = findNextInstruction(element) ?: return
        val usedRegisters = findUsedRegisters(nextInstruction)
        
        if (usedRegisters.contains(destReg)) {
            holder.newAnnotation(HighlightSeverity.WARNING, "Load delay slot violation: register '$destReg' is used immediately after being loaded. On the PSX R3000, data from a load instruction is not available until two instructions later. Using it in the next instruction will result in stale data. Add a 'nop' or another instruction between them.")
                .create()
        }
    }

    private fun findNextInstruction(element: PsiElement): PsiElement? {
        var next = element.parent
        while (next != null) {
            val sibling = next.nextSibling
            if (sibling != null) {
                val instruction = findFirstInstructionIn(sibling)
                if (instruction != null) return instruction
                
                // If not in this sibling, check next siblings of this level
                var currentSibling = sibling.nextSibling
                while (currentSibling != null) {
                    val instr = findFirstInstructionIn(currentSibling)
                    if (instr != null) return instr
                    currentSibling = currentSibling.nextSibling
                }
            }
            next = next.parent
        }
        return null
    }

    private fun findFirstInstructionIn(element: PsiElement): PsiElement? {
        if (element is LeafPsiElement && element.elementType == MipsTokenTypes.KEYWORD) return element
        
        var child = element.firstChild
        while (child != null) {
            val found = findFirstInstructionIn(child)
            if (found != null) return found
            child = child.nextSibling
        }
        return null
    }

    private fun findDestRegister(element: PsiElement): String? {
        // In most MIPS loads, the first register is the destination.
        // e.g., lw $t0, 0($t1)
        var sibling = element.nextSibling
        while (sibling != null) {
            val reg = findFirstRegisterIn(sibling)
            if (reg != null) return reg.text.lowercase()
            sibling = sibling.nextSibling
        }
        return null
    }

    private fun findUsedRegisters(instruction: PsiElement): Set<String> {
        val result = mutableSetOf<String>()
        // We look for all registers in the instruction's "line" (or sibling block)
        // Since our parser is flat, we have to be careful.
        // For now, let's assume everything after the keyword until the next keyword/newline is part of the instruction.
        
        var sibling = instruction.nextSibling
        while (sibling != null) {
            if (sibling is LeafPsiElement && sibling.elementType == MipsTokenTypes.KEYWORD) break
            
            collectRegisters(sibling, result)
            sibling = sibling.nextSibling
        }
        
        // Note: This logic is simple and might include the destination register of the second instruction.
        // However, for Load Delay Slots, even if it's the destination of the NEXT instruction, it's still a conflict
        // if it uses the value (e.g., addu $t0, $t0, $t1).
        return result
    }

    private fun collectRegisters(element: PsiElement, result: MutableSet<String>) {
        if (element is LeafPsiElement && element.elementType == MipsTokenTypes.REGISTER) {
            result.add(element.text.lowercase())
        }
        var child = element.firstChild
        while (child != null) {
            collectRegisters(child, result)
            child = child.nextSibling
        }
    }

    private fun findFirstRegisterIn(element: PsiElement): PsiElement? {
        if (element is LeafPsiElement && element.elementType == MipsTokenTypes.REGISTER) return element
        var child = element.firstChild
        while (child != null) {
            val found = findFirstRegisterIn(child)
            if (found != null) return found
            child = child.nextSibling
        }
        return null
    }
}
